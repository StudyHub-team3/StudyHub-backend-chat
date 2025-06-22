package com.studyhub.study_chat.api.dto;

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
                case SYSTEM_STUDY_CREW_JOINED, SYSTEM_STUDY_CREW_QUITED ->
                    SystemStudyCrewMoveEventResponse.toDto(chatMessage);
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
            return new UserReplyEventResponse(chatMessage.getContent(), chatMessage.getSpeakerId(), chatMessage.getReplyFor());
        }
    }

    public record SystemStudyCrewMoveEventResponse(
        Long speakerId
    ) {
        public static SystemStudyCrewMoveEventResponse toDto(ChatMessage chatMessage) {
            return new SystemStudyCrewMoveEventResponse(chatMessage.getSpeakerId());
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
