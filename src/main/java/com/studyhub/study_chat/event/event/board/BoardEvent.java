package com.studyhub.study_chat.event.event.board;

import com.studyhub.study_chat.common.exception.NotFound;
import com.studyhub.study_chat.domain.Chat;
import com.studyhub.study_chat.domain.ChatMessage;
import com.studyhub.study_chat.domain.MessageType;
import com.studyhub.study_chat.event.event.KafkaEvent;
import com.studyhub.study_chat.event.event.KafkaEventToChatMessage;

import java.time.LocalDateTime;

public record BoardEvent(
    BoardEventType eventType,
    BoardEventData data,
    LocalDateTime timestamp
) implements KafkaEvent {
    public ChatMessage toChatMessage(Chat studyChat) {
        switch (eventType) {
            case BOARD_CREATED -> {
                return ChatMessage.builder()
                    .studyChat(studyChat)
                    .speakerId(data.authorId)
                    .messageType(MessageType.SYSTEM_BOARD_CREATED)
                    .boardId(data.boardId)
                    .build();
            }
            default -> throw new NotFound("처리되지 않은 이벤트가 발생했습니다.");
        }
    }

    public record BoardEventData(
        Long studyId,
        Long boardId,
        Long authorId
    ) implements KafkaEventToChatMessage {
    }
}
