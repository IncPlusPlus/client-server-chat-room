package io.github.incplusplus.chatroom.server;

import java.time.Instant;

public class MessageBuilder {
	private Instant timestamp;
	private String sender;
	private String body;
	
	public MessageBuilder setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
		return this;
	}
	
	public MessageBuilder setSender(String sender) {
		this.sender = sender;
		return this;
	}
	
	public MessageBuilder setBody(String body) {
		this.body = body;
		return this;
	}
	
	public Message createMessage() {
		return new Message(timestamp, sender, body);
	}
}