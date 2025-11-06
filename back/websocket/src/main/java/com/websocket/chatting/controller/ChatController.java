package com.websocket.chatting.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.websocket.chatting.dto.Message;
import com.websocket.chatting.dto.MessageWithUsername;
import com.websocket.chatting.service.MessageService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ChatController {
	@Autowired
    private MessageService messageService;
	
	private final SimpMessagingTemplate messagingTemplate;
	// 채팅방별 접속자 관리
    private final Map<String, Set<Integer>> roomUsers = new HashMap<>();
    
    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

//    @MessageMapping("/send") 
//    @SendTo("/sub/messages")
	@MessageMapping("/send/{roomId}")
    public Message sendMessage(@DestinationVariable String roomId, @Payload Message message) {
		log.info("Room [{}] 메시지: {}", roomId, message);
		Message newMessage = new Message(message.getChatRoomId(), message.getFromId(),
										message.getContent(), message.getIsRead());	
		System.out.println(newMessage.toString());
		
		if (message.getType() == Message.MessageType.JOIN) {
            roomUsers.computeIfAbsent(roomId, k -> new HashSet<>()).add(message.getFromId());
            message.setContent("님이 입장하셨습니다");
        } else if (message.getType() == Message.MessageType.LEAVE) {
            Set<Integer> users = roomUsers.get(roomId);
            if (users != null) {
                users.remove(message.getFromId());
            }
            message.setContent(message.getFromId() + "님이 퇴장했습니다.");
            
        } else if (message.getType() == Message.MessageType.CHAT) {
        	messageService.addMessage(newMessage);
        }
		
		// 직접 목적지를 지정해서 전송
		// convertAndSend : 모든 구독자에게 메시지 브로드캐스트
		
		// 메시지
        messagingTemplate.convertAndSend("/sub/messages/" + roomId, message);
        
        // 접속자 목록
        Set<Integer> users = roomUsers.getOrDefault(roomId, Collections.emptySet());
        messagingTemplate.convertAndSend("/sub/users/" + roomId, users);
        
        return message;
    }
	
	// 채팅기록 불러오기
	@PostMapping("/chat_history")
	public List<MessageWithUsername> getChatHistory(@RequestBody Map<String, Object> payload) {
		int chatroomId = Integer.parseInt(payload.get("roomId").toString());
		String userId = payload.get("userId").toString();
		List<MessageWithUsername> messages = messageService.getMessages(chatroomId, userId);
		
		messagingTemplate.convertAndSend("/sub/messages/" + chatroomId, messages);
		
		return messages;
	}
	
}