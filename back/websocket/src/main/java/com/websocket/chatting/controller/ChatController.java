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
	
	// 임시 메시지 버퍼
	private final Map<Integer, List<Message>> unsavedMessages = new HashMap<>();
	
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
		
		Message newMessage = new Message(
		        message.getChatRoomId(),
		        message.getFromId(),
		        message.getContent(),
		        message.getIsRead()
		    );	
		
		switch (message.getType()) {
	        case JOIN :
	            roomUsers.computeIfAbsent(roomId, k -> new HashSet<>()).add(message.getFromId());
	            message.setContent("님이 입장하셨습니다.");
	            break;	
	        case LEAVE :
	            Set<Integer> users = roomUsers.get(roomId);
	            if (users != null) users.remove(message.getFromId());
	            message.setContent(message.getFromId() + "님이 퇴장했습니다.");
	
	            // 방에서 나가는 시점에 임시 저장 메시지를 한 번에 DB로 저장
//	            List<Message> messagesToSave = unsavedMessages.remove(message.getChatRoomId());
//	            if (messagesToSave != null && !messagesToSave.isEmpty()) {
//	                messageService.saveAllMessages(messagesToSave);
//	                log.info("{}개의 메시지를 DB에 저장했습니다.", messagesToSave.size());
//	            }
	            break;
	        case CHAT :
	        	messageService.addMessage(message);
	        default :
	            // DB에 즉시 저장하지 않고, 메모리에 임시 저장
//	            unsavedMessages.computeIfAbsent(message.getChatRoomId(), k -> new java.util.ArrayList<>())
//	                           .add(newMessage);
	            break;
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
	@PostMapping("/chatroom/chat-history")
	public List<Message> getChatHistory(@RequestBody Map<String, Object> payload) {
		int roomId = Integer.parseInt(payload.get("roomId").toString());
		int fromId = Integer.parseInt(payload.get("fromId").toString());
	    int toId = Integer.parseInt(payload.get("toId").toString());

	    log.info("채팅 내역 요청: fromId={}, toId={}", fromId, toId);

	    List<Message> messages = messageService.getMessagesBetweenUsers(fromId, toId);
	    log.info("조회된 메시지 수 = {}", messages.size());

	    messagingTemplate.convertAndSend("/sub/messages/" + roomId, messages);
	    return messages;
	}
	
}