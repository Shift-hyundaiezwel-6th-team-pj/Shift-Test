package com.project.shift.chat.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.shift.chat.entity.ChatroomEntity;
import com.project.shift.chat.repository.ChatroomRepository;

@Service
public class ChatroomDAO {

	@Autowired
	ChatroomRepository chatroomRepo;
	
	public List<ChatroomEntity> getUserChatrooms (int user){
		return chatroomRepo.findByFromUserId(user);
	}
	
	public List<Integer> findChatroomIdsForUsers(int fromId, int toId) {
		return chatroomRepo.findChatroomIdsForUsers(fromId, toId);
	}
}
