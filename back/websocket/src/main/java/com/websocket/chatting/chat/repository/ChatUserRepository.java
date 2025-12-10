package com.project.shift.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.shift.chat.entity.ChatUserEntity;

public interface ChatUserRepository extends JpaRepository<ChatUserEntity, Long>{
	ChatUserEntity findByUserId(int userId);
}
