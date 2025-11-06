package com.websocket.chatting.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.websocket.chatting.dto.User;

public interface UserRepository extends JpaRepository<User, Integer>{

}
