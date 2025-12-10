package com.websocket.chatting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.websocket.chatting.dao.MessageDAO;
import com.websocket.chatting.dto.Message;

@Service
public class MessageService {
	@Autowired
	MessageDAO dao;
	
	public void addMessage(Message message) {
		dao.insertMessage(message);
	}
	
	public List<Message> getAllMessages(int chatroomId) {
		return dao.getAllMessages(chatroomId);
	}
	
	public List<Message> getMessagesBetweenUsers(int fromId, int toId){
		return dao.getMessagesBetweenUsers(fromId, toId);
	}
	
	public void saveAllMessages (List<Message> messages) {
		dao.saveAllMessages(messages);
	}
}
