package com.project.shift.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.shift.chat.entity.ChatroomEntity;

public interface ChatroomRepository extends JpaRepository<ChatroomEntity, Long>{

	List<ChatroomEntity> findByFromUserId(int userId);
	
	// 두 사용자가 서로 대화하는 방의 ID 조회(사용자별로 chatroomId가 다름)
    @Query("SELECT c.id FROM ChatroomEntity c " +
           "WHERE (c.fromUserId = :fromId AND c.toUserId = :toId) " +
           "		OR (c.fromUserId = :toId AND c.toUserId = :fromId)")
    List<Integer> findChatroomIdsForUsers(int fromId, int toId);
    
}
