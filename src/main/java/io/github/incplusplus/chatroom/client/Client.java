package io.github.incplusplus.chatroom.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static io.github.incplusplus.chatroom.client.VariousConnectionMethods.makeFirstContact;
import static io.github.incplusplus.chatroom.shared.MiscUtils.promptForSocket;
import static io.github.incplusplus.chatroom.shared.StupidSimpleLogger.enable;
import static io.github.incplusplus.chatroom.shared.StupidSimpleLogger.log;

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
		log(in.readLine());
		//		outToServer.writeBytes(msg("Hello!"));
	}
}
