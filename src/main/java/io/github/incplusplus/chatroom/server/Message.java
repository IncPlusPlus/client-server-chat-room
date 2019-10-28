package io.github.incplusplus.chatroom.server;

import java.time.Instant;

/**
 * Represents a chat message
 */
public class Message {
	private Instant timestamp;
	private Server.ClientHandler sender;
}
