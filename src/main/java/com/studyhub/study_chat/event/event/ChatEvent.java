package com.studyhub.study_chat.event.event;

import com.studyhub.study_chat.domain.MessageType;

import java.time.LocalDateTime;

public record ChatEvent(
    Long studyChatMessageId,
    Long studyChatId,
    String message,
    Long speakerId,
    LocalDateTime createdAt,
    MessageType messageType
) {
}
