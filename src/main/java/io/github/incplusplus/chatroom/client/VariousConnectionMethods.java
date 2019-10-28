package io.github.incplusplus.chatroom.client;

import io.github.incplusplus.chatroom.shared.Constants;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static io.github.incplusplus.chatroom.shared.Constants.ConstantEnums.IDENTIFY;
import static io.github.incplusplus.chatroom.shared.Constants.ConstantEnums.SERVER_NAME;
import static io.github.incplusplus.chatroom.shared.MiscUtils.decode;
import static io.github.incplusplus.chatroom.shared.MiscUtils.getHeader;
import static io.github.incplusplus.chatroom.shared.StupidSimpleLogger.log;

public class VariousConnectionMethods {
	public static void makeFirstContact(Socket sock, DataOutputStream outToServer, BufferedReader in,
	                                    ClientType clientType) throws IOException {
		String fromServer = in.readLine();
		log("From server: " + fromServer);
		//expecting server to identify itself by name
		if(fromServer == null || !getHeader(fromServer).equals(SERVER_NAME)) {
			throw new IllegalStateException("The server failed to identify itself upon contact!");
		}
		
		//expecting the server to ask for the client type
		fromServer = in.readLine();
		log("From server: " + fromServer);
		//expecting server to identify itself by name
		if(fromServer == null || !getHeader(fromServer).equals(IDENTIFY)) {
			throw new IllegalStateException("The server failed to ask for client identity upon contact!");
		}
	}
}
