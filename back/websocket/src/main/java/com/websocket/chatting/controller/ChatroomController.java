package com.websocket.chatting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.websocket.chatting.dto.Chatroom;
import com.websocket.chatting.service.ChatroomService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ChatroomController {

	@Autowired
	ChatroomService chatroomService;
	
	@GetMapping("chatrooms/user/{userId}")
	public List<Chatroom> getUserChatroomList (@PathVariable int userId){
		List<Chatroom> chatrooms = chatroomService.getUserChatrooms(userId);
		return chatrooms;
	}
}
