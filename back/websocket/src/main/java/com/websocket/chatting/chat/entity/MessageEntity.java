package com.project.shift.chat.entity;

import java.util.Date;

import com.project.shift.chat.dto.MessageDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name="MESSAGES")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageEntity {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "SEQ_MESSAGES"
    )
    @SequenceGenerator(
        name = "SEQ_MESSAGES",
        sequenceName = "SEQ_MESSAGES",
        allocationSize = 1
    )
    @Column(name = "MESSAGE_ID")
    private long messageId;

    @Column(name = "CHATROOM_ID", nullable = false)
    private long chatroomId;

    @Column(name = "IS_FROM_USER", length = 1)
    private String isFromUser; // 'Y' 또는 'N'

    @Column(name = "IS_READ", length = 1)
    private String isRead; // 'Y' 또는 'N'

    @Column(name = "SENT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sentDate;

    @Column(name = "MESSAGE_CONTENT", length = 300)
    private String content;

    @Column(name = "IS_GIFT", length = 1)
    private String isGift; // 'Y' 또는 'N'

    @Column(name = "IS_EMOJI", length = 1)
    private String isEmoji; // 'Y' 또는 'N'
    
    // DTO -> Entity 변환
    public static MessageEntity toEntity(MessageDTO dto) {
        return MessageEntity.builder()
                .messageId(dto.getMessageId())
                .chatroomId(dto.getChatroomId())
                .isFromUser(dto.getIsFromUser())
                .isRead(dto.getIsRead())
                .sentDate(dto.getSentDate())
                .content(dto.getContent())
                .isGift(dto.getIsGift())
                .isEmoji(dto.getIsEmoji())
                .build();
    }

}
