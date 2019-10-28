package io.github.incplusplus.chatroom.shared;

import io.github.incplusplus.chatroom.client.ClientType;

/**
 * A class of constants that the server and/or the client understand
 */
public class Constants {
	/**
	 * This is the character that separates the beginning header
	 * from the payload of the message. The header is what tells
	 * the server or client what the interaction is regarding.
	 */
	//  :) I love the bell character
	final static char HEADER_SEPARATOR = (char) 7;
	
	/**
	 * This enum contains values that are processed by the server
	 * and/or client
	 */
	public enum ConstantEnum {
		/**
		 * Indicates to the server that the new connection it has
		 * just received is for a headless-style client where
		 * the client can send only in one console and passively receive
		 * in the other. This message would be sent by the writable console.
		 */
		CONNECT_HEADLESS_CLIENT_SENDER,
		/**
		 * Same as {@link #CONNECT_HEADLESS_CLIENT_SENDER} but
		 * this message would come from the reading-only console.
		 */
		CONNECT_HEADLESS_CLIENT_RECEIVER,
		/**
		 * Asks the server to register this new client.
		 * This command will only be accepted while this
		 * client has not yet registered itself.
		 */
		REGISTER_NEW_CLIENT,
		/**
		 * This is sent by the server to tell the client
		 * its name.
		 */
		SERVER_NAME,
		/**
		 * Sent from the server indicating the
		 * client should identify itself as one
		 * of the known {@link ClientType}s
		 */
		IDENTIFY,
		/**
		 * Sent from the client as a response to
		 * the {@link #IDENTIFY} query.
		 */
		IDENTITY,
		/**
		 * Indicates that this is to be treated as a
		 * regular chat message from a client.
		 */
		MESSAGE;
	}
}
