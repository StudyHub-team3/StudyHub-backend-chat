package com.studyhub.study_chat.event.event.studyMember;

import com.studyhub.study_chat.common.exception.NotFound;
import com.studyhub.study_chat.domain.Chat;
import com.studyhub.study_chat.domain.ChatMessage;
import com.studyhub.study_chat.domain.MessageType;
import com.studyhub.study_chat.event.event.KafkaEvent;
import com.studyhub.study_chat.event.event.KafkaEventToChatMessage;
import com.studyhub.study_chat.event.event.chat.ChatEvent;
import com.studyhub.study_chat.event.event.chat.ChatEvent.SystemCrewMoveEventResponse;
import com.studyhub.study_chat.remote.studyMember.dto.StudyRole;

import java.time.LocalDateTime;

import static com.studyhub.study_chat.domain.MessageType.SYSTEM_STUDY_CREW_JOINED;
import static com.studyhub.study_chat.domain.MessageType.SYSTEM_STUDY_CREW_QUITED;

public record StudyMemberEvent(
    StudyMemberEventType eventType,
    StudyMemberEventData data,
    LocalDateTime timestamp
) implements KafkaEvent {
    public ChatMessage toChatMessage(Chat studyChat) {
        switch (eventType) {
            case STUDY_CREW_JOINED -> {
                return ChatMessage.builder()
                    .studyChat(studyChat)
                    .speakerId(data.userId)
                    .messageType(SYSTEM_STUDY_CREW_JOINED)
                    .content(data.userName)
                    .build();
            }
            case STUDY_CREW_QUITED -> {
                return ChatMessage.builder()
                    .studyChat(studyChat)
                    .speakerId(data.userId)
                    .messageType(MessageType.SYSTEM_STUDY_CREW_QUITED)
                    .content(data.userName)
                    .build();
            }
            default -> throw new NotFound("처리되지 않은 이벤트가 발생했습니다.");
        }
    }

    public ChatEvent kafkaEventToChatEvent(ChatMessage chatMessage) {
        return switch (eventType) {
            case STUDY_CREW_JOINED -> new ChatEvent(
                SYSTEM_STUDY_CREW_JOINED,
                new SystemCrewMoveEventResponse(
                    chatMessage.getStudyChat().getId(),
                    chatMessage.getId(),
                    data.userId,
                    data.userName,
                    data.role
                ),
                timestamp
            );
            case STUDY_CREW_QUITED -> new ChatEvent(
                SYSTEM_STUDY_CREW_QUITED,
                new SystemCrewMoveEventResponse(
                    chatMessage.getStudyChat().getId(),
                    chatMessage.getId(),
                    data.userId,
                    data.userName,
                    data.role
                ),
                timestamp
            );
        };
    }

    public record StudyMemberEventData(
        Long studyId,
        Long userId,
        String userName,
        StudyRole role
    ) implements KafkaEventToChatMessage {
    }
}
