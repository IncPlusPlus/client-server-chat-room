package io.github.incplusplus.chatroom.client;

import io.github.incplusplus.chatroom.server.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static io.github.incplusplus.chatroom.client.VariousConnectionMethods.makeFirstContact;
import static io.github.incplusplus.chatroom.client.VariousConnectionMethods.registerReceiver;
import static io.github.incplusplus.chatroom.shared.Constants.ConstantEnum.*;
import static io.github.incplusplus.chatroom.shared.Constants.SHARED_MAPPER;
import static io.github.incplusplus.chatroom.shared.MiscUtils.*;
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
		registerReceiver(kb, outToServer, in);
		System.out.println("Connected! Messages from you and others will appear below.");
		String lineFromServer;
		while (!sock.isClosed()) {
			lineFromServer = in.readLine();
			if (getHeader(lineFromServer).equals(DISCONNECT)) {
				in.close();
				outToServer.close();
				sock.close();
			}
			else if (getHeader(lineFromServer).equals(MESSAGE)) {
				Message m = SHARED_MAPPER.readValue(decodeMessage(lineFromServer), Message.class);
				log(m.toString());
			}
		}
	}
}
