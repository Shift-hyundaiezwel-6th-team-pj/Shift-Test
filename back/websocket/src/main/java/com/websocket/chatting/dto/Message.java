package com.websocket.chatting.dto;

import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Entity
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor
@Table(name="Message")
public class Message {
	public enum MessageType {
        CHAT, JOIN, LEAVE
    }
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
	@Column(name = "CHATROOMID")
    private int chatRoomId;
    @Column(name = "FROMID")
    private int fromId;
    @Transient
    private int toId;
    @Setter
    @Column(name = "CONTENT")
    private String content;
    @Transient //DB와 매핑하지 않는 필드
	private MessageType type;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @Column(name = "SENDDATE")
    private Date sendDate;
    @Column(name = "ISREAD")
    private String isRead;
    
    // 메시지 입력을 위한 생성자
    public Message(int chatRoomId, int fromId, String content, String isRead) {
        this.chatRoomId = chatRoomId;
        this.fromId = fromId;
        this.content = content;
        this.isRead = isRead;
    }
    
}