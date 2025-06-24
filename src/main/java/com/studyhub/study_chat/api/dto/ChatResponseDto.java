package com.studyhub.study_chat.api.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.studyhub.study_chat.domain.ChatMessage;
import com.studyhub.study_chat.domain.MessageType;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;

public class ChatResponseDto {
    public record ChatHistoryResponse(
        Long studyChatId,
        Slice<ChatMessageResponse> chatMessages,
        LocalDateTime threshold // TODO 참여 사용자 정보 반환
    ) {
        public static ChatHistoryResponse toDto(Long studyChatId, Slice<ChatMessage> chatMessageSlice, LocalDateTime prevThreshold) {
            Optional<ChatMessage> oldestMessage = chatMessageSlice.getContent().stream().min(Comparator.comparing(ChatMessage::getCreatedAt));
            return new ChatHistoryResponse(
                studyChatId,
                new SliceImpl<>(
                    chatMessageSlice.getContent().stream().map(ChatMessageResponse::toDto).toList(),
                    chatMessageSlice.getPageable(),
                    chatMessageSlice.hasNext()
                ),
                oldestMessage.map(ChatMessage::getCreatedAt).orElse(prevThreshold)
            );
        }
    }

    public record ChatMessageResponse(
        MessageType eventType,
        ChatMessageData data,
        LocalDateTime timestamp
    ) {
        public static ChatMessageResponse toDto(ChatMessage chatMessage) {
            ChatMessageData content = switch (chatMessage.getMessageType()) {
                case USER_MESSAGE -> UserMessageMessageResponse.toDto(chatMessage);
                case USER_REPLY -> UserReplyMessageResponse.toDto(chatMessage);
                case SYSTEM_STUDY_CREW_JOINED, SYSTEM_STUDY_CREW_QUITED ->
                    SystemCrewMoveMessageResponse.toDto(chatMessage);
                case SYSTEM_BOARD_CREATED -> SystemBoardCreatedMessageResponse.toDto(chatMessage);
            };
            return new ChatMessageResponse(
                chatMessage.getMessageType(),
                content,
                chatMessage.getCreatedAt()
            );
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "eventType")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = UserMessageMessageResponse.class, name = "USER_MESSAGE"),
        @JsonSubTypes.Type(value = UserReplyMessageResponse.class, name = "USER_REPLY"),
        @JsonSubTypes.Type(value = SystemCrewMoveMessageResponse.class, name = "SYSTEM_STUDY_CREW_JOINED"),
        @JsonSubTypes.Type(value = SystemCrewMoveMessageResponse.class, name = "SYSTEM_STUDY_CREW_QUITED"),
        @JsonSubTypes.Type(value = SystemBoardCreatedMessageResponse.class, name = "SYSTEM_BOARD_CREATED"),
    })
    public interface ChatMessageData {
        Long studyChatId();

        Long studyChatMessageId();
    }

    public record UserMessageMessageResponse(
        Long studyChatId,
        Long studyChatMessageId,
        String content,
        Long speakerId
    ) implements ChatMessageData {
        public static UserMessageMessageResponse toDto(ChatMessage chatMessage) {
            return new UserMessageMessageResponse(
                chatMessage.getStudyChat().getId(),
                chatMessage.getId(),
                chatMessage.getContent(),
                chatMessage.getSpeakerId()
            );
        }
    }

    public record UserReplyMessageResponse(
        Long studyChatId,
        Long studyChatMessageId,
        String content,
        Long speakerId,
        Long replyForChatMessageId,
        String replyForChatMessageAuthorName,
        String replyForChatMessageContent
    ) implements ChatMessageData {
        public static UserReplyMessageResponse toDto(ChatMessage chatMessage) {
            return new UserReplyMessageResponse(
                chatMessage.getStudyChat().getId(),
                chatMessage.getId(),
                chatMessage.getContent(),
                chatMessage.getSpeakerId(),
                chatMessage.getReplyForChatMessageId(),
                chatMessage.getReplyForChatMessageAuthorName(),
                chatMessage.getReplyForChatMessageContent()
            );
        }
    }

    public record SystemCrewMoveMessageResponse(
        Long studyChatId,
        Long studyChatMessageId,
        Long userId,
        String userName
    ) implements ChatMessageData {
        public static SystemCrewMoveMessageResponse toDto(ChatMessage chatMessage) {
            return new SystemCrewMoveMessageResponse(
                chatMessage.getStudyChat().getId(),
                chatMessage.getId(),
                chatMessage.getSpeakerId(),
                chatMessage.getContent()
            );
        }
    }

    public record SystemBoardCreatedMessageResponse(
        Long studyChatId,
        Long studyChatMessageId,
        Long authorId,
        Long boardId
    ) implements ChatMessageData {
        public static SystemBoardCreatedMessageResponse toDto(ChatMessage chatMessage) {
            return new SystemBoardCreatedMessageResponse(
                chatMessage.getStudyChat().getId(),
                chatMessage.getId(),
                chatMessage.getSpeakerId(),
                chatMessage.getBoardId()
            );
        }
    }
}
