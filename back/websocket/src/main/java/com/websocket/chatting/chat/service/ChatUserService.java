package com.project.shift.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.shift.chat.dao.ChatUserDAO;
import com.project.shift.chat.dto.ChatUserDTO;
import com.project.shift.chat.entity.ChatUserEntity;

@Service
public class ChatUserService {

	@Autowired
	ChatUserDAO dao;
	
	public ChatUserDTO getUserInfo (int id){
		ChatUserEntity entity = dao.getUserInfo(id);
		if (entity != null) {
			ChatUserDTO dto = ChatUserDTO.toDto(entity);
			return dto;
		}
		return null;
	}
}
