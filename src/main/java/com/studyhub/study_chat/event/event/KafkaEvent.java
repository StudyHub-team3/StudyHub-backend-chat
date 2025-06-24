package com.studyhub.study_chat.event.event;

import com.studyhub.study_chat.domain.Chat;
import com.studyhub.study_chat.domain.ChatMessage;
import com.studyhub.study_chat.event.event.chat.ChatEvent;

import java.time.LocalDateTime;

public interface KafkaEvent {
    Enum eventType();

    KafkaEventToChatMessage data();

    LocalDateTime timestamp();

    ChatMessage toChatMessage(Chat studyChat);

    ChatEvent kafkaEventToChatEvent(ChatMessage chatMessage);
}
