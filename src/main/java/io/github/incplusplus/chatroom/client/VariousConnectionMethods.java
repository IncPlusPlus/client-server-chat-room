package io.github.incplusplus.chatroom.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import static io.github.incplusplus.chatroom.client.ClientType.WRITER;
import static io.github.incplusplus.chatroom.shared.Constants.ConstantEnum.*;
import static io.github.incplusplus.chatroom.shared.MiscUtils.getHeader;
import static io.github.incplusplus.chatroom.shared.MiscUtils.msg;
import static io.github.incplusplus.chatroom.shared.StupidSimpleLogger.log;

public class VariousConnectionMethods {
	public static void makeFirstContact(Socket sock, PrintWriter outToServer, BufferedReader in,
	                                    ClientType clientType) throws IOException {
		String fromServer = in.readLine();
		log("From server: " + fromServer);
		//expecting server to identify itself by name
		if (fromServer == null || !getHeader(fromServer).equals(SERVER_NAME)) {
			throw new IllegalStateException("The server failed to identify itself upon contact!");
		}
		
		//expecting the server to ask for the client type
		fromServer = in.readLine();
		log("From server: " + fromServer);
		//expecting server to identify itself by name
		if (fromServer == null || !getHeader(fromServer).equals(IDENTIFY)) {
			throw new IllegalStateException("The server failed to ask for client identity upon contact!");
		}
		//identify ourselves
		outToServer.println(msg(String.valueOf(WRITER), IDENTITY));
		fromServer = in.readLine();
		//expecting server ask our name
		if (fromServer == null || !getHeader(fromServer).equals(PROVIDE_CLIENT_NAME)) {
			throw new IllegalStateException("The server failed to ask for client identity upon contact!");
		}
		//introduce ourselves
		outToServer.println(msg("Ryan", CLIENT_NAME));
	}
}
