package com.websocket.chatting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.websocket.chatting.dto.Message;
import com.websocket.chatting.dto.MessageWithUsername;

public interface MessageRepository extends JpaRepository<Message, Integer>{

	@Query(   "SELECT new com.websocket.chatting.dto.MessageWithUsername( "
			+ "       com.websocket.chatting.dto.MessageWithUsername$MessageType.CHAT, "
			+ "       u.userId, "
			+ "       m.content) "
			+ "FROM Message m "
			+ "JOIN Users u "
			+ "    ON m.fromId = u.id "
			+ "JOIN Chatroom c "
			+ "    ON m.chatRoomId = c.id "
			+ "JOIN Chatroom base "
			+ "    ON ( "
			+ "         (c.fromId = base.fromId AND c.toId = base.toId) "
			+ "      OR (c.fromId = base.toId   AND c.toId = base.fromId) "
			+ "    ) "
			+ "JOIN Users me "
			+ "    ON (me.userId = :userId) "
			+ "WHERE base.id = :chatRoomId "
			+ "  AND (c.fromId = me.id OR c.toId = me.id) "
			+ "ORDER BY m.sendDate ASC, m.id ASC")
	List<MessageWithUsername> getMessages(@Param("chatRoomId") int chatRoomId,
										  @Param("userId") String userId);
	
}
