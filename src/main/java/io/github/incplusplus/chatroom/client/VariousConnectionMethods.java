package io.github.incplusplus.chatroom.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static io.github.incplusplus.chatroom.client.ClientType.RECEIVER;
import static io.github.incplusplus.chatroom.client.ClientType.WRITER;
import static io.github.incplusplus.chatroom.shared.Constants.ConstantEnum.*;
import static io.github.incplusplus.chatroom.shared.MiscUtils.getHeader;
import static io.github.incplusplus.chatroom.shared.MiscUtils.msg;
import static io.github.incplusplus.chatroom.shared.StupidSimpleLogger.log;

public class VariousConnectionMethods {
	private static String name = "Paul";
	
	public static void makeFirstContact(Socket sock, PrintWriter outToServer, BufferedReader in,
	                                    ClientType clientType, String name) throws IOException {
		VariousConnectionMethods.name =name;
		makeFirstContact(sock,outToServer,in,clientType);
	}
	
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
		if(clientType.equals(WRITER)) {
			outToServer.println(msg(String.valueOf(WRITER), IDENTITY));
			fromServer = in.readLine();
			//expecting server ask our name
			if (fromServer == null || !getHeader(fromServer).equals(PROVIDE_CLIENT_NAME)) {
				throw new IllegalStateException("The server failed to ask for client name upon contact!");
			}
			//introduce ourselves
			outToServer.println(msg(name, CLIENT_NAME));
		}
		else if(clientType.equals(RECEIVER)) {
			//now the registerReceiver method should be run
			
			//prompt user for registration key
			//at this point, the interaction should
			//be written such that the client identifies itself.
			//This has not been implemented here to avoid having more parameters.
		}
	}
	
	public static void registerReceiver(Scanner kb, PrintWriter outToServer, BufferedReader in) throws IOException {
		outToServer.println(msg(String.valueOf(RECEIVER), IDENTITY));
		String fromServer = in.readLine();
		//expecting server ask for registration key
		if (fromServer == null || !getHeader(fromServer).equals(PROVIDE_REG_KEY)) {
			throw new IllegalStateException("The server failed to ask for client registration key upon contact!");
		}
		System.out.print("Registration key: ");
		outToServer.println(msg(kb.nextLine(), REG_KEY));
		fromServer = in.readLine();
		if(fromServer == null || !getHeader(fromServer).equals(CONTINUE)) {
			throw new IllegalStateException("The server failed to acknowledge client registration key message!");
		}
		if(getHeader(fromServer).equals(REG_KEY_REJECTED)) {
			System.out.println("That key was not recognized by the server. Try again.");
			registerReceiver(kb, outToServer, in);
		}
	}
}
