package com.studyhub.study_chat.service;

import com.studyhub.study_chat.api.dto.ChatRequestDto.ChatMessageRequest;
import com.studyhub.study_chat.api.dto.ChatResponseDto.ChatEvent;
import com.studyhub.study_chat.api.dto.ChatResponseDto.ChatHistory;
import com.studyhub.study_chat.common.exception.BadParameter;
import com.studyhub.study_chat.domain.Chat;
import com.studyhub.study_chat.domain.ChatMessage;
import com.studyhub.study_chat.domain.repository.ChatMessageRepository;
import com.studyhub.study_chat.domain.repository.ChatRepository;
import com.studyhub.study_chat.event.event.KafkaEventToChatMessage;
import com.studyhub.study_chat.event.event.study.StudyEvent;
import com.studyhub.study_chat.event.event.study.StudyEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public void publishChat(Long studyChatId, Long speakerId, ChatMessageRequest request) {
        Chat chat = chatRepository.findById(studyChatId)
            .orElseThrow(() -> new BadParameter("존재하지 않는 채팅방입니다"));
        ChatMessage replyForChatMessage = null;
        if (request.replyForChatMessageId() != null) {
            replyForChatMessage = chatMessageRepository.findById(request.replyForChatMessageId())
                .orElseThrow(() -> new BadParameter("존재하지 않는 채팅 메시지입니다"));
        }
        ChatMessage chatMessage = chatMessageRepository.save(request.toEntity(speakerId, chat, replyForChatMessage));
        applicationEventPublisher.publishEvent(ChatEvent.toDto(chatMessage));
    }

    @Transactional
    public void handleEvent(KafkaEventToChatMessage event) {
        if (StudyEventType.STUDY_CREATED.equals(event.eventType())) {
            createStudyChat((StudyEvent) event);
            return;
        }
        Chat chat = chatRepository.findByStudyId(event.studyId())
            .orElseThrow(() -> new BadParameter("존재하지 않는 채팅방입니다"));
        publishChat(event.toChatMessage(chat));
    }

    private void createStudyChat(StudyEvent event) {
        if (chatRepository.findByStudyId(event.studyId()).isPresent())
            throw new BadParameter("이미 채팅방이 존재합니다");
        chatRepository.save(Chat.builder().studyId(event.studyId()).build());
    }

    private void publishChat(ChatMessage eventChatMessage) {
        if (eventChatMessage != null) {
            ChatMessage chatMessage = chatMessageRepository.save(eventChatMessage);
            applicationEventPublisher.publishEvent(ChatEvent.toDto(chatMessage));
        }
    }

    @Transactional(readOnly = true)
    public ChatHistory getHistory(Long studyId, LocalDateTime threshold, int amount) {
        Chat chat = chatRepository.findByStudyId(studyId)
            .orElseThrow(() -> new BadParameter("존재하지 않는 채팅방입니다"));
        Slice<ChatMessage> chatMessageSlice = chatMessageRepository.getChatMessagesByStudyChatAndCreatedAtIsLessThan(
            chat,
            threshold,
            PageRequest.of(0, amount, Sort.Direction.DESC, "createdAt")
        );
        return ChatHistory.toDto(chat.getId(), chatMessageSlice, threshold);
    }
}
