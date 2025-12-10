package com.project.shift.chat.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.project.shift.chat.dto.ChatroomDTO;

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
@Table(name="CHATROOMS")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatroomEntity {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "SEQ_CHATROOMS"
    )
    @SequenceGenerator(
        name = "SEQ_CHATROOMS",
        sequenceName = "SEQ_CHATROOMS",
        allocationSize = 1
    )
    @Column(name = "CHATROOM_ID")
    private long chatroomId;

    @Column(name = "FROM_USER_ID")
    private long fromUserId;

    @Column(name = "TO_USER_ID")
    private long toUserId;

    @Column(name = "CHATROOM_NAME")
    private String chatroomName;

    @Column(name = "CONNECTION_STATUS", length = 2)
    private String connectionStatus; // 'ON' 또는 'OF'

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "CONNECTION_TIME")
    private Date connectionTime;

    @Column(name = "IS_DARK_MODE", length = 1)
    private String isDarkMode; // DEFAULT 'N', 'Y' 또는 'N'
    
    // DTO -> Entity 변환
    public static ChatroomEntity toEntity(ChatroomDTO dto) {
        return ChatroomEntity.builder()
                .chatroomId(dto.getChatroomId())
                .fromUserId(dto.getFromUserId())
                .toUserId(dto.getToUserId())
                .chatroomName(dto.getChatroomName())
                .connectionStatus(dto.getConnectionStatus())
                .connectionTime(dto.getConnectionTime())
                .isDarkMode(dto.getIsDarkMode())
                .build();
    }

}
