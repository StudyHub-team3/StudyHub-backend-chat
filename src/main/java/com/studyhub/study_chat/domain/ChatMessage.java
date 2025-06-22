package com.studyhub.study_chat.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity(name = "study_chat_message")
@EntityListeners(AuditingEntityListener.class)
public class ChatMessage {
    @Id
    @Column(name = "study_chat_message_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "study_chat_id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Chat studyChat;
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    @Column(name = "speaker_id")
    private Long speakerId;
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType;
    @Column(name = "reply_for_chat_message_id")
    private Long replyFor;
    @Column(name = "board_id")
    private Long boardId;
}
