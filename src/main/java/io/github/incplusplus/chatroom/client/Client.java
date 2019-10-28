package io.github.incplusplus.chatroom.client;

import java.io.*;
import java.net.Socket;

import static io.github.incplusplus.chatroom.client.VariousConnectionMethods.makeFirstContact;
import static io.github.incplusplus.chatroom.shared.MiscUtils.msg;
import static io.github.incplusplus.chatroom.shared.MiscUtils.promptForSocket;
import static io.github.incplusplus.chatroom.shared.StupidSimpleLogger.enable;

public class Client {
	private static Socket sock;
	private static PrintWriter outToServer;
	private static BufferedReader in;
	
	public static void main(String[] args) throws IOException {
		enable();
		sock = promptForSocket();
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		outToServer = new PrintWriter(sock.getOutputStream(), true);
		makeFirstContact(sock, outToServer, in, ClientType.WRITER);
		//		outToServer.writeBytes(msg("Hello!"));
	}
}
