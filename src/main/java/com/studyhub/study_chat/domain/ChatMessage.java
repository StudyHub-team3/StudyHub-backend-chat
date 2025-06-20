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
    private Chat chatId;
    @Column(name = "study_id", nullable = false)
    private String content;
    @Column(name = "speaker_id", nullable = false)
    private Long speakerId;
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private MessageType messageType;
}
