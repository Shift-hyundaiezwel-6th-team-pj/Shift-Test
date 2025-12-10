package com.project.shift.chat.dao;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.shift.chat.entity.ChatUserEntity;
import com.project.shift.chat.repository.ChatUserRepository;

@Service
public class ChatUserDAO {

	@Autowired
	ChatUserRepository userRepo;
	
	public ChatUserEntity getUserInfo (int id){
		return userRepo.findByUserId(id);
	}
}
