package com.websocket.chatting.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import com.websocket.chatting.model.ChatMessage;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ChatController {
	private final SimpMessagingTemplate messagingTemplate;
	// 채팅방별 접속자 관리
    private final Map<String, Set<String>> roomUsers = new HashMap<>();
    
    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

//    private ChatService chatService;

//    @MessageMapping("/send") 
//    @SendTo("/sub/messages")
	@MessageMapping("/send/{roomId}")
    public ChatMessage sendMessage(@DestinationVariable String roomId, @Payload ChatMessage message) {
		log.info("Room [{}] 메시지: {}", roomId, message);
		
		if (message.getType() == ChatMessage.MessageType.JOIN) {
            roomUsers.computeIfAbsent(roomId, k -> new HashSet<>()).add(message.getSender());
            message.setContent("님이 입장하셨습니다");
        } else if (message.getType() == ChatMessage.MessageType.LEAVE) {
            Set<String> users = roomUsers.get(roomId);
            if (users != null) {
                users.remove(message.getSender());
            }
            message.setContent(message.getSender() + "님이 퇴장했습니다.");
        }
		
		// 직접 목적지를 지정해서 전송
		// convertAndSend : 모든 구독자에게 메시지 브로드캐스트
		
		// 메시지
        messagingTemplate.convertAndSend("/sub/messages/" + roomId, message);
        
        // 접속자 목록
        Set<String> users = roomUsers.getOrDefault(roomId, Collections.emptySet());
        messagingTemplate.convertAndSend("/sub/users/" + roomId, users);
        
        return message;
    }
}