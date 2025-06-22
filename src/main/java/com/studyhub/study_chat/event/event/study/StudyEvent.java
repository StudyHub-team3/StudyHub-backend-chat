package com.studyhub.study_chat.event.event.study;

import com.studyhub.study_chat.common.exception.NotFound;
import com.studyhub.study_chat.domain.Chat;
import com.studyhub.study_chat.domain.ChatMessage;
import com.studyhub.study_chat.domain.MessageType;
import com.studyhub.study_chat.event.event.KafkaEventToChatMessage;

public record StudyEvent(
    StudyEventType eventType,
    Long studyId,
    Long userId
) implements KafkaEventToChatMessage {
    public ChatMessage toChatMessage(Chat studyChat) {
        switch (eventType) {
            case STUDY_CREATED -> {
                return null;
            }
            case STUDY_CREW_JOINED -> {
                return ChatMessage.builder()
                    .studyChat(studyChat)
                    .speakerId(userId)
                    .messageType(MessageType.SYSTEM_STUDY_CREW_JOINED)
                    .build();
            }
            case STUDY_CREW_QUITED -> {
                return ChatMessage.builder()
                    .studyChat(studyChat)
                    .speakerId(userId)
                    .messageType(MessageType.SYSTEM_STUDY_CREW_QUITED)
                    .build();
            }
            default -> throw new NotFound("처리되지 않은 이벤트가 발생했습니다.");
        }
    }
}
