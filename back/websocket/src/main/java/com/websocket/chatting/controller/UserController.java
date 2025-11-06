package com.websocket.chatting.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.websocket.chatting.dto.User;
import com.websocket.chatting.service.UserService;

@RestController
public class UserController {

	@Autowired
	UserService userService;
	
	@GetMapping("/users/{id}")
	public ResponseEntity<?> getUserInfo (@PathVariable int id){
		Optional<User> user = userService.getUserInfo(id);
		
		if (user.isPresent()) {
            // 유저가 존재하면 200 OK + JSON 반환
            return ResponseEntity.ok(user.get());
        } else {
            // 유저가 없으면 404 Not Found + 메시지 반환
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("User not found");
        }
	}
}
