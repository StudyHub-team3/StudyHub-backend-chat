package com.studyhub.study_chat.event.event.board;

import com.studyhub.study_chat.common.exception.NotFound;
import com.studyhub.study_chat.domain.Chat;
import com.studyhub.study_chat.domain.ChatMessage;
import com.studyhub.study_chat.domain.MessageType;
import com.studyhub.study_chat.event.event.KafkaEventToChatMessage;

public record BoardEvent(
    BoardEventType eventType,
    Long studyId,
    Long boardId,
    Long authorId
) implements KafkaEventToChatMessage {
    public ChatMessage toChatMessage(Chat studyChat) {
        switch (eventType) {
            case BOARD_CREATED -> {
                return ChatMessage.builder()
                    .studyChat(studyChat)
                    .speakerId(authorId)
                    .messageType(MessageType.SYSTEM_BOARD_CREATED)
                    .boardId(boardId)
                    .build();
            }
            default -> throw new NotFound("처리되지 않은 이벤트가 발생했습니다.");
        }
    }
}
