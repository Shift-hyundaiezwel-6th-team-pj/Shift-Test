package com.websocket.chatting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.websocket.chatting.dto.Chatroom;

public interface ChatroomRepository extends JpaRepository<Chatroom, Integer>{

	List<Chatroom> findByFromId(int userId);
}
