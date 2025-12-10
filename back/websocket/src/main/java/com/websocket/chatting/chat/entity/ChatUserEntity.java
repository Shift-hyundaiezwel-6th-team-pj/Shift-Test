package com.project.shift.chat.entity;

import com.project.shift.chat.dto.ChatUserDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="USERS")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatUserEntity {
	
    @Id
    @GeneratedValue(
	    strategy = GenerationType.SEQUENCE,
	    generator = "SEQ_USERS"
	)
	@SequenceGenerator(
	    name = "SEQ_USERS",
	    sequenceName = "SEQ_USERS",
	    allocationSize = 1
	)
    @Column(name = "USER_ID")
    private long userId;

    @Column(name = "LOGIN_ID", unique = true)
    private String loginId;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "NAME")
    private String name;

    @Column(name = "PHONE", unique = true)
    private String phone;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "POINTS")
    private int points; // DEFAULT 0

    @Column(name = "REFRESH_TOKEN")
    private String refreshToken;

    @Column(name = "ADMIN_FLAG", length = 1)
    private String adminFlag; // DEFAULT 'N', 'Y' 또는 'N'
    
    // DTO -> Entity 변환
    public static ChatUserEntity toEntity(ChatUserDTO dto) {
        return ChatUserEntity.builder()
                .userId(dto.getUserId())
                .loginId(dto.getLoginId())
                .password(dto.getPassword())
                .name(dto.getName())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .points(dto.getPoints())
                .refreshToken(dto.getRefreshToken())
                .adminFlag(dto.getAdminFlag())
                .build();
    }
    
}
