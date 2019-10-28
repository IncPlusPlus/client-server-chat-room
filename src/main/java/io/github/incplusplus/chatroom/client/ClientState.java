package io.github.incplusplus.chatroom.client;

/**
 * Represents the state that a client could be in.
 */
public enum ClientState {
	/**
	 * The client has taken no action yet besides
	 * initiating a connection.
	 */
	CONNECTED,
	/**
	 * The client has registered their name.
	 */
	REGISTERED,
	/**
	 * The client has connected their listener
	 * and has begun to listen. At this point,
	 * they're all set up to participate.
	 */
	LISTENING,
	/**
	 * Something happened to this connection.
	 * It's no longer valid.
	 */
	INVALID,
	/**
	 * They're gone!
	 */
	DISCONNECTED;
}
