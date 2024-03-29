package io.github.incplusplus.chatroom.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.incplusplus.chatroom.client.ClientState;
import io.github.incplusplus.chatroom.client.ClientType;
import io.github.incplusplus.chatroom.shared.StupidSimpleLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.Instant;
import java.util.*;

import static io.github.incplusplus.chatroom.client.ClientState.*;
import static io.github.incplusplus.chatroom.client.ClientType.RECEIVER;
import static io.github.incplusplus.chatroom.client.ClientType.WRITER;
import static io.github.incplusplus.chatroom.server.ServerMethods.*;
import static io.github.incplusplus.chatroom.shared.Constants.ConstantEnum.*;
import static io.github.incplusplus.chatroom.shared.Constants.SHARED_MAPPER;
import static io.github.incplusplus.chatroom.shared.MiscUtils.*;
import static io.github.incplusplus.chatroom.shared.StupidSimpleLogger.log;

public class Server {
	private static ServerSocket socket;
	private final static int port = 1234;
	private static String serverName = "Chatroom Server";
	private static final List<ClientHandler> clientHandles = new ArrayList<>();
	private static final List<Message> messages = new ArrayList<>();
	
	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(System.in);
		//Set up my custom logging implementation
		StupidSimpleLogger.enable();
		if (serverName != null)
			System.out.println("Server name: " + serverName);
		socket = new ServerSocket(port);
		start(port);
		System.out.println("Server started on " + getIp() + ":" + port + ".");
		System.out.println("Hit enter to stop the server.");
		/*
		 * Wait for newline from user.
		 * This call will block the main thread
		 * until the user hits enter in the console.
		 * This is because the server runs on a daemon thread.
		 * This feels like a cleaner way than having a while(true){}
		 * on the main thread.
		 */
		in.nextLine();
		System.out.println("Server stopped.");
	}
	
	static void start(int port) {
		class ServerStartTask implements Runnable {
			int port;
			
			ServerStartTask(int p) {port = p;}
			
			public void run() {
				try {
					System.out.println("Ready and waiting!");
					while (true) {
						try {
							ClientHandler ch = new ClientHandler(socket.accept());
							//TODO: Fix the improper usage of synchronizedList, you dolt!
							Collections.synchronizedList(clientHandles).add(ch);
							ch.start();
						}
						catch (IOException e) {
							e.printStackTrace();
							log("FATAL ERROR. THE CLIENT HANDLER ENCOUNTERED AN ERROR DURING CLOSING TIME");
						}
					}
				}
				finally {
					log("Server shutting down!");
				}
			}
		}
		Thread t = new Thread(new ServerStartTask(port));
		t.setDaemon(true);
		t.start();
	}
	
	static class ClientHandler extends Thread {
		private Socket writerSocket;
		private Socket receiverSocket;
		private PrintWriter writerOut;
		private PrintWriter receiverOut;
		private BufferedReader writerIn;
		private BufferedReader receiverIn;
		private volatile ClientState clientState;
		private String clientName;
		private UUID clientUUID;
		private int clientRegistrationKey;
		private ClientType clientType;
		
		ClientHandler(Socket currentConnection) {
			this.clientUUID = UUID.randomUUID();
			this.clientState = CONNECTING;
			this.writerSocket = currentConnection;
		}
		
		public void run() {
			try {
				clientState = CONNECTED;
				writerOut = new PrintWriter(writerSocket.getOutputStream(), true);
				writerIn = new BufferedReader(new InputStreamReader(writerSocket.getInputStream()));
				log("New client connected at " + writerSocket.getLocalAddress()
						+ ":" + writerSocket.getLocalPort());
				//welcome the client (not the user)
				writerOut.println(msg(serverName, SERVER_NAME));
				//tell client to identify what ClientType it is
				clientType = ClientType.valueOf(negotiate(IDENTIFY, IDENTITY, writerOut, writerIn));
				//handle the incoming connections
				configure(clientType, writerOut, writerIn);
			}
			catch (SocketException e) {
				broadcast(
						new MessageBuilder().setTimestamp(Instant.now()).setBody(
								"User '" + this.getClientName() + "' has disconnected.").setSender("").createMessage()
				);
				clientState = INVALID;
				try {
					disconnect();
				}
				catch (IOException ex) {
					ex.printStackTrace();
				}
				
			}
			catch (IOException e) {
				e.printStackTrace();
				System.out.println("FATAL ERROR. AN ERROR ESCAPED OUT INTO THE CLIENT HANDLER'S THREAD.RUN() METHOD");
				clientState = INVALID;
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//<editor-fold desc="Configuration methods">
		private void configure(ClientType clientType, PrintWriter out,
		                       BufferedReader in) throws IOException, InterruptedException {
			if (clientType.equals(WRITER))
				configureWriter(out, in).startWhenReady();
			else if (clientType.equals((RECEIVER)))
				configureReader(out, in);
			else throw new IllegalStateException("The provided ClientType '" + clientType + "' isn't supported.");
		}
		
		private ClientHandler configureReader(PrintWriter out, BufferedReader in) throws IOException {
			int providedKey = Integer.parseInt(negotiate(PROVIDE_REG_KEY, REG_KEY, out, in));
			ClientHandler handlerForKey = null;
			synchronized (clientHandles) {
				//Find the client whose registration key matches that of what this
				//new client has provided.
				handlerForKey = getHandlerForKey(clientHandles, providedKey);
			}
			if (handlerForKey == null) {
				out.println(REG_KEY_REJECTED);
				return configureReader(out, in);
			}
			out.println(CONTINUE);
			handlerForKey.clientState = CONNECTED;
			//reset key so more become available
			handlerForKey.clientRegistrationKey = 0;
			
			//attach this reader to the existing writer
			//calling the getWriter*() methods because this is initially assumed to be a writer
			handlerForKey.attachReceiver(getWriterSocket(), getWriterOut(), getWriterIn());
			//remove this from the clients list as this is not a unique client but a receiver
			//TODO: Fix the improper usage of synchronizedList, you dolt!
			Collections.synchronizedList(clientHandles).remove(this);
			//Invite the handler for this client to start participating
			return handlerForKey;
		}
		
		private ClientHandler configureWriter(PrintWriter out, BufferedReader in) throws IOException {
			this.clientName = negotiate(PROVIDE_CLIENT_NAME, CLIENT_NAME, out, in);
			broadcast(
					new MessageBuilder().setTimestamp(Instant.now()).setBody(
							"User '" + this.getClientName() + "' is connecting.").setSender("").createMessage()
			);
			synchronized (clientHandles) {
				this.clientRegistrationKey = getNewRegKey(clientHandles);
			}
			this.clientState = REGISTERED;
			out.println("Run ClientWindow.main() and enter " + clientRegistrationKey + " when prompted.");
			return this;
		}
		//</editor-fold>
		
		public void attachReceiver(Socket receiverSocket, PrintWriter receiverOut, BufferedReader receiverIn) {
			this.receiverSocket = receiverSocket;
			this.receiverOut = receiverOut;
			this.receiverIn = receiverIn;
			this.clientState = LISTENING;
		}
		
		public void disconnect() throws IOException {
			this.clientState = DISCONNECTED;
			this.writerOut.println(DISCONNECT);
			this.receiverOut.println(DISCONNECT);
			this.writerSocket.close();
			this.receiverSocket.close();
			broadcast(
					new MessageBuilder().setTimestamp(Instant.now()).setBody(
							"User '" + this.getClientName() + "' has disconnected.").setSender("").createMessage()
			);
		}
		
		/**
		 * Broadcast a message to all clients except that
		 * with the specified UUID.
		 * @param message the message to broadcast
		 * @param uuid the UUID of the client to exclude
		 *             from this broadcast
		 */
		public void broadcastExcept(Message message, UUID uuid) {
			//TODO: Fix the improper usage of synchronizedList, you dolt!
			Collections.synchronizedList(messages).add(message);
			log(message.toString());
			synchronized (clientHandles) {
				clientHandles.stream()
						.filter(ClientHandler::isListening)
						.filter(clientHandler -> !clientHandler.getClientUUID().equals(uuid))
						.forEach(clientHandler -> {
							try {
								clientHandler.receiveMessage(message);
							}
							catch (JsonProcessingException e) {
								e.printStackTrace();
							}
						});
			}
			
		}
		
		/**
		 * Broadcast a message to all clients
		 * @param message the message to broadcast
		 */
		public void broadcast(Message message) {
			//TODO: Fix the improper usage of synchronizedList, you dolt!
			Collections.synchronizedList(messages).add(message);
			log(message.toString());
			synchronized (clientHandles) {
				clientHandles.stream()
						.filter(ClientHandler::isListening)
						.forEach(clientHandler -> {
							try {
								clientHandler.receiveMessage(message);
							}
							catch (JsonProcessingException e) {
								e.printStackTrace();
							}
						});
			}
		}
		
		public void receiveMessage(Message message) throws JsonProcessingException {
			getReceiverOut().println(msg(SHARED_MAPPER.writeValueAsString(message)));
		}
		
		public void startWhenReady() throws IOException, InterruptedException {
			if (clientState.equals(INVALID))
				return;
			//block until we are listening
			while (!clientState.equals(LISTENING)) {}
			//broadcast that the user has fully connected
			broadcastExcept(
					new MessageBuilder().setTimestamp(Instant.now()).setBody(
							"User '" + this.getClientName() + "' has connected.").setSender("").createMessage(),
					getClientUUID()
			);
			//get the new client up to speed
			synchronized (messages) {
				for (Message m : messages) {
					receiveMessage(m);
				}
			}
			//start the whole listening process
			while (clientState.equals(LISTENING)) {
				if (writerIn != null && receiverOut != null) {
					String message;
					if (!getWriterSocket().isClosed() && !getReceiverSocket().isClosed()) {
						message = writerIn.readLine();
						switch (getHeader(message)) {
							case MESSAGE:
								Message m = new MessageBuilder().setBody(decodeMessage(message))
										.setSender(this.getClientName()).setTimestamp(Instant.now()).createMessage();
								broadcast(m);
								break;
							case DISCONNECT:
								disconnect();
								break;
						}
					}
				}
				else {
					this.clientState = INVALID;
					disconnect();
				}
			}
		}
		
		public boolean isListening() {
			return getClientState().equals(LISTENING);
		}
		
		//<editor-fold desc="Getters">
		public Socket getWriterSocket() {
			return writerSocket;
		}
		
		public Socket getReceiverSocket() {
			return receiverSocket;
		}
		
		public PrintWriter getWriterOut() {
			return writerOut;
		}
		
		public PrintWriter getReceiverOut() {
			return receiverOut;
		}
		
		public BufferedReader getWriterIn() {
			return writerIn;
		}
		
		public BufferedReader getReceiverIn() {
			return receiverIn;
		}
		
		public ClientState getClientState() {
			return clientState;
		}
		
		public String getClientName() {
			return clientName;
		}
		
		public UUID getClientUUID() {
			return clientUUID;
		}
		
		public int getClientRegistrationKey() {
			return clientRegistrationKey;
		}
		//</editor-fold>
	}
}
