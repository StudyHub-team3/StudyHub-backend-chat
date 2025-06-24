package com.studyhub.study_chat.event.event.chat;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.studyhub.study_chat.common.exception.BadParameter;
import com.studyhub.study_chat.common.exception.UnreachableError;
import com.studyhub.study_chat.domain.Chat;
import com.studyhub.study_chat.domain.ChatMessage;
import com.studyhub.study_chat.domain.MessageType;
import com.studyhub.study_chat.event.event.KafkaEvent;
import com.studyhub.study_chat.event.event.KafkaEventToChatMessage;
import com.studyhub.study_chat.remote.studyMember.dto.StudyRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public record ChatEvent(
    MessageType eventType,
    ChatEventData data,
    LocalDateTime timestamp
) implements KafkaEvent {
    private static final Logger log = LoggerFactory.getLogger(ChatEvent.class);

    public static ChatEvent toChatEvent(ChatMessage chatMessage) {
        ChatEventData content = switch (chatMessage.getMessageType()) {
            case USER_MESSAGE -> UserEventEventResponse.toDto(chatMessage);
            case USER_REPLY -> UserReplyEventResponse.toDto(chatMessage);
            default -> throw new BadParameter("시스템 메세지는 사용자가 생성할 수 없습니다.");
        };
        return new ChatEvent(
            chatMessage.getMessageType(),
            content,
            chatMessage.getCreatedAt()
        );
    }

    public ChatMessage toChatMessage(Chat studyChat) {
        log.error("ChatEvent를 다시 publish해서는 안됩니다.", new UnreachableError(""));
        return null;
    }

    public ChatEvent kafkaEventToChatEvent(ChatMessage chatMessage) {
        return this;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "eventType")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = UserEventEventResponse.class, name = "USER_MESSAGE"),
        @JsonSubTypes.Type(value = UserReplyEventResponse.class, name = "USER_REPLY"),
        @JsonSubTypes.Type(value = SystemCrewMoveEventResponse.class, name = "SYSTEM_STUDY_CREW_JOINED"),
        @JsonSubTypes.Type(value = SystemCrewMoveEventResponse.class, name = "SYSTEM_STUDY_CREW_QUITED"),
        @JsonSubTypes.Type(value = SystemBoardCreatedEventResponse.class, name = "SYSTEM_BOARD_CREATED"),
    })
    public interface ChatEventData extends KafkaEventToChatMessage {
        Long studyChatId();

        Long studyChatMessageId();
    }

    public record UserEventEventResponse(
        Long studyChatId,
        Long studyChatMessageId,
        String content,
        Long speakerId
    ) implements ChatEventData {
        public static UserEventEventResponse toDto(ChatMessage chatMessage) {
            return new UserEventEventResponse(
                chatMessage.getStudyChat().getId(),
                chatMessage.getId(),
                chatMessage.getContent(),
                chatMessage.getSpeakerId()
            );
        }

        public Long studyId() {
            throw new UnreachableError("ChatEvent는 studyId를 사용하지 않습니다.");
        }
    }

    public record UserReplyEventResponse(
        Long studyChatId,
        Long studyChatMessageId,
        String content,
        Long speakerId,
        Long replyForChatMessageId,
        String replyForChatMessageAuthorName,
        String replyForChatMessageContent
    ) implements ChatEventData {
        public static UserReplyEventResponse toDto(ChatMessage chatMessage) {
            return new UserReplyEventResponse(
                chatMessage.getStudyChat().getId(),
                chatMessage.getId(),
                chatMessage.getContent(),
                chatMessage.getSpeakerId(),
                chatMessage.getReplyForChatMessageId(),
                chatMessage.getReplyForChatMessageAuthorName(),
                chatMessage.getReplyForChatMessageContent()
            );
        }

        public Long studyId() {
            throw new UnreachableError("ChatEvent는 studyId를 사용하지 않습니다.");
        }
    }

    public record SystemCrewMoveEventResponse(
        Long studyChatId,
        Long studyChatMessageId,
        Long userId,
        String userName,
        StudyRole userRole
    ) implements ChatEventData {
        public Long studyId() {
            throw new UnreachableError("ChatEvent는 studyId를 사용하지 않습니다.");
        }
    }

    public record SystemBoardCreatedEventResponse(
        Long studyChatId,
        Long studyChatMessageId,
        Long authorId,
        Long boardId
    ) implements ChatEventData {
        public Long studyId() {
            throw new UnreachableError("ChatEvent는 studyId를 사용하지 않습니다.");
        }
    }
}
