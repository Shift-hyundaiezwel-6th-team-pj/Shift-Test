package com.websocket.chatting.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.websocket.chatting.dto.Chatroom;
import com.websocket.chatting.repository.ChatroomRepository;

@Service
public class ChatroomDAO {

	@Autowired
	ChatroomRepository chatroomRepo;
	
	public List<Chatroom> getUserChatrooms (int user){
		return chatroomRepo.findByFromId(user);
	}
	
	public List<Integer> findChatroomIdsForUsers(int fromId, int toId) {
		return chatroomRepo.findChatroomIdsForUsers(fromId, toId);
	}
}
