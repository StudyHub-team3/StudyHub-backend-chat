package com.studyhub.study_chat.event.event;

import com.studyhub.study_chat.domain.Chat;
import com.studyhub.study_chat.domain.ChatMessage;

public interface KafkaEventToChatMessage {
    Long studyId();

    ChatMessage toChatMessage(Chat studyChat);

    Enum eventType();
}
