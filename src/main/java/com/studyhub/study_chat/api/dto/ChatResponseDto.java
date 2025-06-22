package com.studyhub.study_chat.api.dto;

import com.studyhub.study_chat.domain.ChatMessage;
import com.studyhub.study_chat.domain.MessageType;

import java.time.LocalDateTime;

public class ChatResponseDto {
    public record ChatEvent(
        MessageType messageType,
        Long studyChatId,
        Long studyChatMessageId,
        Object content,
        LocalDateTime createdAt
    ) {
        public static ChatEvent toDto(ChatMessage chatMessage) {
            Object content = switch (chatMessage.getMessageType()) {
                case USER_MESSAGE -> UserMessageEventResponse.toDto(chatMessage);
                case USER_REPLY -> UserReplyEventResponse.toDto(chatMessage);
                case SYSTEM_USER_JOINED -> SystemUserJoinedEventResponse.toDto(chatMessage);
                case SYSTEM_BOARD_CREATED -> SystemBoardCreatedEventResponse.toDto(chatMessage);
            };
            return new ChatEvent(
                chatMessage.getMessageType(),
                chatMessage.getStudyChat().getId(),
                chatMessage.getId(),
                content,
                chatMessage.getCreatedAt()
            );
        }
    }

    public record UserMessageEventResponse(
        String content,
        Long speakerId
    ) {
        public static UserMessageEventResponse toDto(ChatMessage chatMessage) {
            return new UserMessageEventResponse(chatMessage.getContent(), chatMessage.getSpeakerId());
        }
    }

    public record UserReplyEventResponse(
        String content,
        Long speakerId,
        Long replyForChatMessageId
    ) {
        public static UserReplyEventResponse toDto(ChatMessage chatMessage) {
            return new UserReplyEventResponse(chatMessage.getContent(), chatMessage.getSpeakerId(), chatMessage.getReplyFor().getId());
        }
    }

    public record SystemUserJoinedEventResponse(
        Long speakerId
    ) {
        public static SystemUserJoinedEventResponse toDto(ChatMessage chatMessage) {
            return new SystemUserJoinedEventResponse(chatMessage.getSpeakerId());
        }
    }

    public record SystemBoardCreatedEventResponse(
        Long speakerId,
        Long boardId
    ) {
        public static SystemBoardCreatedEventResponse toDto(ChatMessage chatMessage) {
            return new SystemBoardCreatedEventResponse(chatMessage.getSpeakerId(), chatMessage.getBoardId());
        }
    }
}
