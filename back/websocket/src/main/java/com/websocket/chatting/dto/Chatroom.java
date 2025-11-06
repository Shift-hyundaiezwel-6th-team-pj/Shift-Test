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
import lombok.Getter;

@Getter
@Entity
@DynamicInsert
@Table(name="Chatroom")
public class Chatroom {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	@Column(name = "FROMID")
	private int fromId;
	@Column(name = "TOID")
	private int toId;
	@Column(name = "LASTMSGDATE")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date lastMsgDate;
}
