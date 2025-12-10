package com.project.shift.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.project.shift.chat.dto.ChatUserDTO;
import com.project.shift.chat.service.ChatUserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ChatUserController {

	@Autowired
	ChatUserService userService;
	
	@GetMapping("/users/{id}")
	public ResponseEntity<?> getUserInfo (@PathVariable int id){
		ChatUserDTO user = userService.getUserInfo(id);
		log.info("user pk {}", user.getUserId());
		if (user != null) {
            // 유저가 존재하면 200 OK + JSON 반환
            return ResponseEntity.ok(user);
        } else {
            // 유저가 없으면 404 Not Found + 메시지 반환
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("User not found");
        }
	}
}