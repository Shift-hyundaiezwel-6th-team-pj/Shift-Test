package com.websocket.chatting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.websocket.chatting.dto.Chatroom;

public interface ChatroomRepository extends JpaRepository<Chatroom, Integer>{

	List<Chatroom> findByFromId(int userId);
	
	// 두 사용자가 서로 대화하는 방 두 개 조회
    @Query("SELECT c.id FROM Chatroom c " +
           "WHERE (c.fromId = :fromId AND c.toId = :toId) " +
           "   OR (c.fromId = :toId AND c.toId = :fromId)")
    List<Integer> findChatroomIdsForUsers(int fromId, int toId);
}
