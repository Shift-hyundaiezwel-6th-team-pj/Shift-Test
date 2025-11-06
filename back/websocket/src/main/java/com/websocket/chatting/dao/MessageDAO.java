package com.websocket.chatting.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.websocket.chatting.dto.Message;
import com.websocket.chatting.repository.MessageRepository;

@Service
public class MessageDAO {

	@Autowired
	MessageRepository messageRepo;
	
	public void insertMessage (Message message) {
		messageRepo.save(message);
	}
}
