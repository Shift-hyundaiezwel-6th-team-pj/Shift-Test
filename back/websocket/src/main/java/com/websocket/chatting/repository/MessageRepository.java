package com.websocket.chatting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.websocket.chatting.dto.Message;

public interface MessageRepository extends JpaRepository<Message, Integer>{

}
