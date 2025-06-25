package com.studyhub.study_chat.event.event.study;

import com.studyhub.study_chat.common.exception.NotFound;
import com.studyhub.study_chat.common.exception.UnreachableError;
import com.studyhub.study_chat.domain.Chat;
import com.studyhub.study_chat.domain.ChatMessage;
import com.studyhub.study_chat.event.event.KafkaEvent;
import com.studyhub.study_chat.event.event.KafkaEventToChatMessage;
import com.studyhub.study_chat.event.event.chat.ChatEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public record StudyEvent(
    StudyEventType eventType,
    StudyEventData data,
    LocalDateTime timestamp
) implements KafkaEvent {
    private static final Logger log = LoggerFactory.getLogger(StudyEvent.class);

    public ChatMessage toChatMessage(Chat studyChat) {
        switch (eventType) {
            case STUDY_CREATED, STUDY_DELETED -> {
                return null;
            }
            default -> throw new NotFound("처리되지 않은 이벤트가 발생했습니다.");
        }
    }

    @Deprecated
    public ChatEvent kafkaEventToChatEvent(ChatMessage chatMessage) {
        log.error("StudyEvent로 ChatEvent를 만들수 없습니다.", new UnreachableError(""));
        return null;
    }

    public record StudyEventData(
        Long studyId,
        Long userId
    ) implements KafkaEventToChatMessage {
    }
}
