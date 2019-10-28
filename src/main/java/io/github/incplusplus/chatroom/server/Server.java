package io.github.incplusplus.chatroom.server;

import io.github.incplusplus.chatroom.client.ClientState;
import io.github.incplusplus.chatroom.client.ClientType;
import io.github.incplusplus.chatroom.shared.StupidSimpleLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.UUID;

import static io.github.incplusplus.chatroom.client.ClientState.*;
import static io.github.incplusplus.chatroom.server.ServerMethods.negotiate;
import static io.github.incplusplus.chatroom.shared.Constants.ConstantEnum.*;
import static io.github.incplusplus.chatroom.shared.MiscUtils.msg;
import static io.github.incplusplus.chatroom.shared.MiscUtils.randInt;
import static io.github.incplusplus.chatroom.shared.StupidSimpleLogger.log;

public class Server {
	private static ServerSocket socket;
	private final static int port = 1234;
	private static String serverName = "Chatroom Server";
	
	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(System.in);
		//Set up my custom logging implementation
		StupidSimpleLogger.enable();
		if (serverName != null)
			System.out.println("Server name: " + serverName);
		start(port);
		System.out.println("Server started on port " + port + ".");
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
					socket = new ServerSocket(port);
					System.out.println("Ready and waiting!");
					while (true) {
						try {
							new ClientHandler(socket.accept()).start();
						}
						catch (IOException e) {
							e.printStackTrace();
							log("FATAL ERROR. THE CLIENT HANDLER ENCOUNTERED AN ERROR DURING CLOSING TIME");
						}
					}
				}
				catch (IOException e) {
					e.printStackTrace();
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
	
	private static class ClientHandler extends Thread {
		private Socket connectionSocket;
		private PrintWriter out;
		private BufferedReader in;
		private ClientType clientType;
		private ClientState clientState;
		private String clientName;
		private UUID clientUUID;
		private int clientRegistrationKey;
		
		ClientHandler(Socket currentConnection) {
			this.connectionSocket = currentConnection;
		}
		
		public void run() {
			try {
				clientState = CONNECTED;
				out = new PrintWriter(connectionSocket.getOutputStream(), true);
				in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
				log("New client connected at " + connectionSocket.getLocalAddress()
						+ ":" + connectionSocket.getLocalPort() + " Performing registration");
				//welcome the client (not the user)
				out.println(msg(serverName, SERVER_NAME));
				//tell client to identify what ClientType it is
				clientType = ClientType.valueOf(negotiate(IDENTIFY, IDENTITY, out, in));
				configure(clientType, out, in);
			}
			catch (IOException e) {
				e.printStackTrace();
				System.out.println("FATAL ERROR. AN ERROR ESCAPED OUT INTO THE CLIENT HANDLER'S THREAD.RUN() METHOD");
				clientState = INVALID;
			}
		}
		
		private void configure(ClientType clientType, PrintWriter out, BufferedReader in) throws IOException {
			if (clientType.equals(ClientType.WRITER))
				configureWriter(out, in);
			else if (clientType.equals((ClientType.RECEIVER)))
				configureReader(out, in);
			else throw new IllegalStateException("The provided ClientType '" + clientType + "' isn't supported.");
		}
		
		private void configureReader(PrintWriter out, BufferedReader in) {
		
		}
		
		private void configureWriter(PrintWriter out, BufferedReader in) throws IOException {
			this.clientName = negotiate(PROVIDE_CLIENT_NAME, CLIENT_NAME, out, in);
			this.clientUUID = UUID.randomUUID();
			this.clientRegistrationKey = randInt(1,10);
			this.clientState = REGISTERED;
			out.println("Run ClientWindow.main() and enter " + clientRegistrationKey + " when prompted.");
		}
	}
}
