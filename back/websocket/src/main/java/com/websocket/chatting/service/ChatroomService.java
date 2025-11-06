package com.websocket.chatting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.websocket.chatting.dao.ChatroomDAO;
import com.websocket.chatting.dto.Chatroom;

@Service
public class ChatroomService {

	@Autowired
	ChatroomDAO dao;
	
	public List<Chatroom> getUserChatrooms (int userId){
		return dao.getUserChatrooms(userId);
	}
}
