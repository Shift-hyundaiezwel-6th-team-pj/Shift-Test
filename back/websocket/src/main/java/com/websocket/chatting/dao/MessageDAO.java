package com.websocket.chatting.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.websocket.chatting.dto.Message;
import com.websocket.chatting.dto.MessageWithUsername;
import com.websocket.chatting.repository.MessageRepository;

@Service
public class MessageDAO {

	@Autowired
	MessageRepository messageRepo;
	
	public void insertMessage (Message message) {
		messageRepo.save(message);
	}
	
	public List<MessageWithUsername> getMessages(int chatroomId, String userId) {
		return messageRepo.getMessages(chatroomId, userId);
	}
}
