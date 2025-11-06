package com.websocket.chatting.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.websocket.chatting.dao.MessageDAO;
import com.websocket.chatting.dto.Message;

@Service
public class ChatService {
	@Autowired
	MessageDAO dao;
	
	public void addMessage(Message message) {
		dao.insertMessage(message);
	}
}
