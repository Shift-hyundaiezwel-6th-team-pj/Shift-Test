package com.websocket.chatting.dao;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.websocket.chatting.dto.User;
import com.websocket.chatting.repository.UserRepository;

@Service
public class UserDAO {

	@Autowired
	UserRepository userRepo;
	
	public Optional<User> getUserInfo (int id){
		return userRepo.findById(id);
	}
}
