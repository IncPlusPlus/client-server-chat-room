package io.github.incplusplus.chatroom.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static io.github.incplusplus.chatroom.client.VariousConnectionMethods.makeFirstContact;
import static io.github.incplusplus.chatroom.shared.Constants.ConstantEnum.DISCONNECT;
import static io.github.incplusplus.chatroom.shared.Constants.QUIT_STRING;
import static io.github.incplusplus.chatroom.shared.MiscUtils.msg;
import static io.github.incplusplus.chatroom.shared.MiscUtils.promptForSocket;
import static io.github.incplusplus.chatroom.shared.StupidSimpleLogger.enable;
import static io.github.incplusplus.chatroom.shared.StupidSimpleLogger.log;

public class Client {
	private static Socket sock;
	private static PrintWriter outToServer;
	private static BufferedReader in;
	private static Scanner kb;
	
	public static void main(String[] args) throws IOException {
		kb = new Scanner(System.in);
		String name;
		enable();
		sock = promptForSocket();
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		outToServer = new PrintWriter(sock.getOutputStream(), true);
		System.out.println("Before we start, what's your name?");
		System.out.print("Name: ");
		name = kb.nextLine();
		System.out.println("Connecting...");
		makeFirstContact(sock, outToServer, in, ClientType.WRITER, name);
		String messageToSend;
		log(in.readLine());
		while (!sock.isClosed()) {
			messageToSend=kb.nextLine();
			if (messageToSend.equals(QUIT_STRING)) {
				outToServer.println(DISCONNECT);
				sock.close();
			}
			else {
				outToServer.println(msg(messageToSend));
			}
		}
		System.out.println("Quit command received. See ya!");
	}
}
