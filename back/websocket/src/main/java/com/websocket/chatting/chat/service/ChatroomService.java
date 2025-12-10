package com.project.shift.chat.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.shift.chat.dao.ChatroomDAO;
import com.project.shift.chat.dto.ChatroomDTO;
import com.project.shift.chat.entity.ChatroomEntity;

@Service
public class ChatroomService {

	@Autowired
	ChatroomDAO dao;
	
	public List<ChatroomDTO> getUserChatrooms(int userId){
		List<ChatroomEntity> entityList = dao.getUserChatrooms(userId);
		List<ChatroomDTO> dtoList = new ArrayList<ChatroomDTO>();
		for (ChatroomEntity e : entityList) {
			dtoList.add(ChatroomDTO.toDto(e));
		}
		return dtoList;
	}
	
	public List<Integer> findChatroomIdsForUsers(int fromId, int toId) {
		return dao.findChatroomIdsForUsers(fromId, toId);
	}
}
