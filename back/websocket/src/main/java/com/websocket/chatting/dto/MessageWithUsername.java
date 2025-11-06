package com.websocket.chatting.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class MessageWithUsername {
	public enum MessageType {
        CHAT, JOIN, LEAVE
    }
	private MessageType type;
	private String sender;
	private String content;
}
