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

//    @MessageMapping("/send") // 클라이언트가 특정 엔드포인트로 전송한 메시지를 서버의 해당 메서드가 처리하도록 매핑
    						// 'pub/send' 경로로 메시지를 보내면 설정에 따라 pub은 prefix로 제거하고
    						//	남은 경로인 send를 기반으로 어떤 메서드가 처리할지 결정
//    @SendTo("/sub/messages") // @MessageMapping으로 처리한 후 메서드의 반환값을 특정 목적지로 보내도록 지정
    						// '/sub/messages'를 구독한 모든 클라이언트에게 해당 메시지를 전달
	@MessageMapping("/send/{roomId}")
    public ChatMessage sendMessage(@DestinationVariable String roomId, @Payload ChatMessage message) {
		log.info("Room [{}] 메시지: {}", roomId, message);
		
		if (message.getType() == ChatMessage.MessageType.JOIN) {
			// .computeIfAbsent : 키가 존재하지 않거나 null인 경우, mappingFunction 람다식(또는 함수)을 사용하여 새로운 값을 생성
			//					: 계산된 값이 null이 아니면, 새로운 키-값 쌍을 맵에 추가
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