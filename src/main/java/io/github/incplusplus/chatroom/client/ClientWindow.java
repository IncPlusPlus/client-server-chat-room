package io.github.incplusplus.chatroom.client;

import io.github.incplusplus.chatroom.shared.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static io.github.incplusplus.chatroom.client.VariousConnectionMethods.makeFirstContact;
import static io.github.incplusplus.chatroom.shared.Constants.ConstantEnum.REG_KEY;
import static io.github.incplusplus.chatroom.shared.MiscUtils.msg;
import static io.github.incplusplus.chatroom.shared.MiscUtils.promptForSocket;
import static io.github.incplusplus.chatroom.shared.StupidSimpleLogger.enable;
import static io.github.incplusplus.chatroom.shared.StupidSimpleLogger.log;

public class ClientWindow {
	private static Socket sock;
	private static PrintWriter outToServer;
	private static BufferedReader in;
	private static Scanner kb;
	
	public static void main(String[] args) throws IOException {
		kb = new Scanner(System.in);
		enable();
		sock = promptForSocket();
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		outToServer = new PrintWriter(sock.getOutputStream(), true);
		System.out.print("Name: ");
		System.out.println("Connecting...");
		makeFirstContact(sock, outToServer, in, ClientType.RECEIVER);
		System.out.print("Registration key: ");
		outToServer.println(msg(kb.nextLine(), REG_KEY));
		System.out.println("Connected! Messages from you and others will appear below.");
		while (true) {
			log(in.readLine());
		}
	}
}
