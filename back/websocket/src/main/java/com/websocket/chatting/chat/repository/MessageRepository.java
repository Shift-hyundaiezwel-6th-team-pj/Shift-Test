package com.project.shift.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.shift.chat.entity.MessageEntity;

public interface MessageRepository extends JpaRepository<MessageEntity, Long>{
		
	// 두 참여자 같이 참여한 채팅방의 메시지 내역 반환
	@Query("SELECT m FROM MessageEntity m " +
            "WHERE m.chatroomId IN :chatroomIds " +
            "ORDER BY m.messageId ASC")
     List<MessageEntity> findMessagesByChatroomIds(@Param("chatroomIds") List<Integer> chatroomIds);

}
