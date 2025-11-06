package com.websocket.chatting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.websocket.chatting.dao.MessageDAO;
import com.websocket.chatting.dto.Message;
import com.websocket.chatting.dto.MessageWithUsername;

@Service
public class MessageService {
	@Autowired
	MessageDAO dao;
	
	public void addMessage(Message message) {
		dao.insertMessage(message);
	}
	public List<MessageWithUsername> getMessages(int chatroomId, String userId) {
		return dao.getMessages(chatroomId, userId);
	}
}
