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
    public record ChatHistory(
        Long studyChatId,
        Slice<ChatEvent> chatEvents,
        LocalDateTime threshold
    ) {
        public static ChatHistory toDto(Long studyChatId, Slice<ChatMessage> chatMessageSlice, LocalDateTime prevThreshold) {
            Optional<ChatMessage> oldestMessage = chatMessageSlice.getContent().stream().min(Comparator.comparing(ChatMessage::getCreatedAt));
            return new ChatHistory(
                studyChatId,
                new SliceImpl<>(
                    chatMessageSlice.getContent().stream().map(ChatEvent::toDto).toList(),
                    chatMessageSlice.getPageable(),
                    chatMessageSlice.hasNext()
                ),
                oldestMessage.map(ChatMessage::getCreatedAt).orElse(prevThreshold)
            );
        }
    }

    public record ChatEvent(
        MessageType eventType,
        StudyChatMessageData data,
        LocalDateTime timestamp
    ) {
        public static ChatEvent toDto(ChatMessage chatMessage) {
            StudyChatMessageData content = switch (chatMessage.getMessageType()) {
                case USER_MESSAGE -> UserMessageEventResponse.toDto(chatMessage);
                case USER_REPLY -> UserReplyEventResponse.toDto(chatMessage);
                case SYSTEM_STUDY_CREW_JOINED, SYSTEM_STUDY_CREW_QUITED ->
                    SystemStudyCrewMoveEventResponse.toDto(chatMessage);
                case SYSTEM_BOARD_CREATED -> SystemBoardCreatedEventResponse.toDto(chatMessage);
            };
            return new ChatEvent(
                chatMessage.getMessageType(),
                content,
                chatMessage.getCreatedAt()
            );
        }
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "eventType")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = UserMessageEventResponse.class, name = "USER_MESSAGE"),
        @JsonSubTypes.Type(value = UserReplyEventResponse.class, name = "USER_REPLY"),
        @JsonSubTypes.Type(value = SystemStudyCrewMoveEventResponse.class, name = "SYSTEM_STUDY_CREW_JOINED"),
        @JsonSubTypes.Type(value = SystemStudyCrewMoveEventResponse.class, name = "SYSTEM_STUDY_CREW_QUITED"),
        @JsonSubTypes.Type(value = SystemBoardCreatedEventResponse.class, name = "SYSTEM_BOARD_CREATED"),
    })
    public interface StudyChatMessageData {
        Long studyChatId();

        Long studyChatMessageId();
    }

    public record UserMessageEventResponse(
        Long studyChatId,
        Long studyChatMessageId,
        String content,
        Long speakerId
    ) implements StudyChatMessageData {
        public static UserMessageEventResponse toDto(ChatMessage chatMessage) {
            return new UserMessageEventResponse(
                chatMessage.getStudyChat().getId(),
                chatMessage.getId(),
                chatMessage.getContent(),
                chatMessage.getSpeakerId()
            );
        }
    }

    public record UserReplyEventResponse(
        Long studyChatId,
        Long studyChatMessageId,
        String content,
        Long speakerId,
        Long replyForChatMessageId
    ) implements StudyChatMessageData {
        public static UserReplyEventResponse toDto(ChatMessage chatMessage) {
            return new UserReplyEventResponse(
                chatMessage.getStudyChat().getId(),
                chatMessage.getId(),
                chatMessage.getContent(),
                chatMessage.getSpeakerId(),
                chatMessage.getReplyFor()
            );
        }
    }

    public record SystemStudyCrewMoveEventResponse(
        Long studyChatId,
        Long studyChatMessageId,
        Long speakerId
    ) implements StudyChatMessageData {
        public static SystemStudyCrewMoveEventResponse toDto(ChatMessage chatMessage) {
            return new SystemStudyCrewMoveEventResponse(
                chatMessage.getStudyChat().getId(),
                chatMessage.getId(),
                chatMessage.getSpeakerId()
            );
        }
    }

    public record SystemBoardCreatedEventResponse(
        Long studyChatId,
        Long studyChatMessageId,
        Long speakerId,
        Long boardId
    ) implements StudyChatMessageData {
        public static SystemBoardCreatedEventResponse toDto(ChatMessage chatMessage) {
            return new SystemBoardCreatedEventResponse(
                chatMessage.getStudyChat().getId(),
                chatMessage.getId(),
                chatMessage.getSpeakerId(),
                chatMessage.getBoardId()
            );
        }
    }
}
