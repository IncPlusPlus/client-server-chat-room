package io.github.incplusplus.chatroom.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static io.github.incplusplus.chatroom.client.VariousConnectionMethods.makeFirstContact;
import static io.github.incplusplus.chatroom.shared.MiscUtils.msg;
import static io.github.incplusplus.chatroom.shared.MiscUtils.promptForSocket;

public class Client {
	private static Socket sock;
	private static DataOutputStream outToServer;
	private static BufferedReader in;
	
	public static void main(String[] args) throws IOException {
		sock = promptForSocket();
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		outToServer = new DataOutputStream(sock.getOutputStream());
		makeFirstContact(sock,outToServer,in,ClientType.WRITER);
//		outToServer.writeBytes(msg("Hello!"));
	}
}
