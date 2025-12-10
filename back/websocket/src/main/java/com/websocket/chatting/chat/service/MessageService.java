package com.project.shift.chat.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.shift.chat.dao.MessageDAO;
import com.project.shift.chat.dto.MessageDTO;
import com.project.shift.chat.entity.MessageEntity;

@Service
public class MessageService {
	
	@Autowired
	MessageDAO dao;
	
	public void addMessage(MessageDTO message) {
		dao.insertMessage(MessageEntity.toEntity(message));
	}
	
	public List<MessageDTO> getMessagesBetweenUsers(int fromId, int toId){
		List<MessageEntity> entityList = dao.getMessagesBetweenUsers(fromId, toId);
		List<MessageDTO> dtoList = new ArrayList<MessageDTO>();
		for (MessageEntity e : entityList) {
			dtoList.add(MessageDTO.toDto(e));
		}
		return dtoList;
	}

}
