package com.studyhub.study_chat.api.dto;

import com.studyhub.study_chat.domain.Chat;
import com.studyhub.study_chat.domain.ChatMessage;
import com.studyhub.study_chat.domain.MessageType;

public class ChatRequestDto {
    public record ChatMessageRequest(
        MessageType messageType,
        String content,
        Long replyForChatMessageId
    ) {
        public ChatMessage toEntity(Long speakerId, Chat studyChat, ChatMessage replyForChatMessage) {
            return ChatMessage.builder()
                .studyChat(studyChat)
                .content(content)
                .speakerId(speakerId)
                .messageType(messageType != null ? messageType : MessageType.USER_MESSAGE)
                .replyFor(replyForChatMessage.getId())
                .build();
        }
    }
}
