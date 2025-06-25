package com.studyhub.study_chat.event.event.board;

import com.studyhub.study_chat.common.exception.NotFound;
import com.studyhub.study_chat.domain.Chat;
import com.studyhub.study_chat.domain.ChatMessage;
import com.studyhub.study_chat.event.event.KafkaEvent;
import com.studyhub.study_chat.event.event.KafkaEventToChatMessage;
import com.studyhub.study_chat.event.event.chat.ChatEvent;
import com.studyhub.study_chat.event.event.chat.ChatEvent.SystemBoardCreatedEventResponse;

import java.time.LocalDateTime;

import static com.studyhub.study_chat.domain.MessageType.SYSTEM_BOARD_CREATED;

public record BoardEvent(
    BoardEventType eventType,
    BoardEventData data,
    LocalDateTime timestamp
) implements KafkaEvent {
    public ChatMessage toChatMessage(Chat studyChat) {
        return switch (eventType) {
            case BOARD_CREATED -> ChatMessage.builder()
                .studyChat(studyChat)
                .speakerId(data.authorId)
                .messageType(SYSTEM_BOARD_CREATED)
                .boardId(data.boardId)
                .build();
            default -> throw new NotFound("처리되지 않은 이벤트가 발생했습니다.");
        };
    }

    public ChatEvent kafkaEventToChatEvent(ChatMessage chatMessage) {
        return switch (eventType) {
            case BOARD_CREATED -> new ChatEvent(
                SYSTEM_BOARD_CREATED,
                new SystemBoardCreatedEventResponse(
                    chatMessage.getStudyChat().getId(),
                    chatMessage.getId(),
                    data.authorId,
                    data.boardId
                ),
                chatMessage.getCreatedAt()
            );
        };
    }

    public record BoardEventData(
        Long studyId,
        Long boardId,
        Long authorId
    ) implements KafkaEventToChatMessage {
    }
}
