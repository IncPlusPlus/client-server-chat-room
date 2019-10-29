package io.github.incplusplus.chatroom.shared;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.SplittableRandom;

import static io.github.incplusplus.chatroom.shared.Constants.ConstantEnum.MESSAGE;
import static io.github.incplusplus.chatroom.shared.Constants.HEADER_SEPARATOR;

public class MiscUtils {
	public static Socket promptForSocket() throws IOException {
		Scanner in = new Scanner(System.in);
		String host;
		int port;
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
	 * @return the decoded message if it exists; else null
	 * @throws IOException if there is no header
	 */
	public static String decode(String receivedMessage) {
		String[] split = receivedMessage.split(Character.toString(HEADER_SEPARATOR));
		if (split.length > 1)
			return split[1];
		return null;
	}
	
	/**
	 * This is the same as {@link #decode(String)} except it
	 * specifically expects the incoming header to be a {@link io.github.incplusplus.chatroom.shared.Constants.ConstantEnum#MESSAGE}
	 */
	public static String decodeMessage(String receivedMessage) {
		assert getHeader(receivedMessage).equals(MESSAGE);
		return decode(receivedMessage);
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
	
	public static int randInt(int lowerBoundInclusive, int upperBoundInclusive) {
		return new SplittableRandom().nextInt(lowerBoundInclusive, upperBoundInclusive + 1);
	}
}
