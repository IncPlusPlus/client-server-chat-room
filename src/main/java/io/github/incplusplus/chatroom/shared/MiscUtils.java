package io.github.incplusplus.chatroom.shared;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import static io.github.incplusplus.chatroom.shared.Constants.ConstantEnum.MESSAGE;
import static io.github.incplusplus.chatroom.shared.Constants.HEADER_SEPARATOR;

public class MiscUtils {
	public static Socket promptForSocket() throws IOException {
		Scanner in = new Scanner(System.in);
		String host;
		int port;
		//I really didn't feel like dealing with JavaFX or Swing
		System.out.println("This window is merely for sending messages. " +
				"To see what's going on. Run the main method of the ClientWindow class.\n");
		System.out.print("Host: ");
		host = in.nextLine();
		System.out.print("Port: ");
		port = in.nextInt();
		//gotta love the Scanner bug
		in.nextLine();
		return new Socket(host, port);
	}
	
	/**
	 * Prefixes the provided string such that it begins with
	 * the message value from the {@link Constants.ConstantEnum} enum
	 * and is also separated from the header by {@link Constants#HEADER_SEPARATOR}
	 *
	 * @param intendedMessage the message to be prefixed
	 * @return a prefixed copy of the provided string
	 */
	public static String msg(String intendedMessage) {
		return msg(intendedMessage, MESSAGE);
	}
	
	/**
	 * Prefixes the provided string such that it begins with
	 * the some provided header value
	 * and is also separated from the header by {@link Constants#HEADER_SEPARATOR}
	 *
	 * @param intendedMessage the message to be prefixed
	 * @param header          the particular header to prefix the string with
	 * @return a prefixed copy of the provided string
	 */
	public static String msg(String intendedMessage, Constants.ConstantEnum header) {
		return String.valueOf(header) +
				HEADER_SEPARATOR +
				intendedMessage;
	}
	
	/**
	 * Decodes a message. This strips the header from the message
	 * and returns only the payload.
	 *
	 * @param receivedMessage the message to decode
	 * @return the decoded message
	 * @throws an {@link IOException} if there is no header
	 *            or no message at all
	 */
	public static String decode(String receivedMessage) {
		return receivedMessage.split(Character.toString(HEADER_SEPARATOR))[1];
	}
	
	/**
	 * Gets the header of a message which contains a header and a payload.
	 *
	 * @param fullPayload the message to get the header from
	 * @return the header of the supplied message
	 */
	public static Constants.ConstantEnum getHeader(String fullPayload) {
		return Constants.ConstantEnum.valueOf(fullPayload.split(Character.toString(HEADER_SEPARATOR))[0]);
	}
}
