package com.websocket.chatting.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.websocket.chatting.dao.UserDAO;
import com.websocket.chatting.dto.User;

@Service
public class UserService {

	@Autowired
	UserDAO dao;
	
	public Optional<User> getUserInfo (int id){
		return dao.getUserInfo(id);
	}
}
