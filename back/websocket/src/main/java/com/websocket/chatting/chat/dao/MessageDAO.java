package com.project.shift.chat.dao;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.shift.chat.entity.MessageEntity;
import com.project.shift.chat.repository.ChatroomRepository;
import com.project.shift.chat.repository.MessageRepository;

@Service
public class MessageDAO {

	@Autowired
	MessageRepository messageRepo;
	
	@Autowired
	ChatroomRepository chatroomRepo;
	
	public List<MessageEntity> getMessagesBetweenUsers(int fromId, int toId) {
        // 두 사용자의 모든 관련 채팅방 ID 조회
        List<Integer> chatroomIds = chatroomRepo.findChatroomIdsForUsers(fromId, toId);

        if (chatroomIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 두 채팅방의 모든 메시지를 불러옴
        return messageRepo.findMessagesByChatroomIds(chatroomIds);
    }
	
	public void insertMessage (MessageEntity message) {
		messageRepo.save(message);
	}
	
}
