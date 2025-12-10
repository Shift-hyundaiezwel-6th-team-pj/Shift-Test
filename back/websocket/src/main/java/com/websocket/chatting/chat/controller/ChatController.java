package com.project.shift.chat.controller;

import java.util.Collections;
import java.util.Date;
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

import com.project.shift.chat.dto.MessageDTO;
import com.project.shift.chat.service.ChatroomService;
import com.project.shift.chat.service.MessageService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ChatController {
	@Autowired
    private MessageService messageService;
	@Autowired
	private ChatroomService chatroomService;
	
	private final SimpMessagingTemplate messagingTemplate;
	// 채팅방별 접속자 관리
    private final Map<String, Set<Integer>> roomUsers = new HashMap<>();
    
    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

	@MessageMapping("/send/{roomId}")
    public MessageDTO sendMessage(@DestinationVariable String roomId, @Payload MessageDTO message) {
		log.info("Room [{}] 메시지: {}", roomId, message);

		message.setSentDate(new Date());
		int fromId = message.getFromId();
		int toId = message.getToId();
		
		switch (message.getType()) {
	        case JOIN :
	            roomUsers.computeIfAbsent(roomId, k -> new HashSet<>()).add(fromId);
	            message.setContent(fromId + "님이 입장했습니다.");
	            System.out.println(message.getFromId() + " : " + message.getContent());
	            break;	
	        case LEAVE :
	            Set<Integer> users = roomUsers.get(roomId);
	            if (users != null) users.remove(fromId);
	            message.setContent(fromId + "님이 퇴장했습니다.");
	            log.info("{} : {}", message.getFromId(), message.getContent());
	
	            break;
	        case CHAT :
	        	// 서버로 날아온 메시지를 DB에 저장
	        	messageService.addMessage(message);
	        	break;
	        default :
	            break;
		}
		
		// 직접 목적지를 지정해서 전송
		// convertAndSend : 모든 구독자에게 메시지 브로드캐스트
		
		// 메시지
		// 메시지를 받는 사람의 채팅방 ID를 추출하기 위한 로직
		List<Integer> chatrooms = chatroomService.findChatroomIdsForUsers(fromId, toId);
		if (chatrooms.size() > 1) {
			chatrooms.removeIf(id -> id == Integer.parseInt(roomId));
			try {
				// 받는 사람의 채팅방 ID 추출
				String partnerChatroomId = chatrooms.get(0).toString();
				
				// 최종적으로 보낸 사람과 받는 사람의 채팅방 ID로 모두 메시지 전송
				messagingTemplate.convertAndSend("/sub/messages/" + roomId, message);
				messagingTemplate.convertAndSend("/sub/messages/" + partnerChatroomId, message);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
        // 접속자 목록
        Set<Integer> users = roomUsers.getOrDefault(roomId, Collections.emptySet());
        messagingTemplate.convertAndSend("/sub/users/" + roomId, users);
        
        return message;
    }
	
	// 채팅기록 불러오기
	@PostMapping("/chatroom/chat-history")
	public List<MessageDTO> getChatHistory(@RequestBody Map<String, Object> payload) {
		int fromId = Integer.parseInt(payload.get("fromId").toString());
	    int toId = Integer.parseInt(payload.get("toId").toString());

	    log.info("채팅 내역 요청: fromId={}, toId={}", fromId, toId);

	    List<MessageDTO> messages = messageService.getMessagesBetweenUsers(fromId, toId);
	    log.info("조회된 메시지 수 = {}", messages.size());

	    return messages;
	}
	
}