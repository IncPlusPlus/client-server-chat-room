package io.github.incplusplus.chatroom.client;

public enum ClientType {
	/**
	 * Indicates that this client is for writing
	 * to the server only.
	 */
	WRITER,
	/**
	 * Indicates that this client is for
	 * receiving from the server only.
	 */
	RECEIVER;
}
