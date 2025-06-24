package com.studyhub.study_chat.service;

import com.studyhub.study_chat.api.dto.ChatRequestDto.ChatMessageRequest;
import com.studyhub.study_chat.api.dto.ChatResponseDto.ChatHistoryResponse;
import com.studyhub.study_chat.common.exception.BadParameter;
import com.studyhub.study_chat.domain.Chat;
import com.studyhub.study_chat.domain.ChatMessage;
import com.studyhub.study_chat.domain.repository.ChatMessageRepository;
import com.studyhub.study_chat.domain.repository.ChatRepository;
import com.studyhub.study_chat.event.event.KafkaEvent;
import com.studyhub.study_chat.event.event.chat.ChatEvent;
import com.studyhub.study_chat.event.event.study.StudyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.studyhub.study_chat.event.event.study.StudyEventType.STUDY_CREATED;
import static com.studyhub.study_chat.event.event.study.StudyEventType.STUDY_DELETED;

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
        if (request.replyForChatMessageId() != null) {
            chatMessageRepository.findById(request.replyForChatMessageId())
                .orElseThrow(() -> new BadParameter("존재하지 않는 채팅 메시지입니다"));
        }
        ChatMessage chatMessage = chatMessageRepository.save(request.toEntity(speakerId, chat));
        applicationEventPublisher.publishEvent(ChatEvent.toChatEvent(chatMessage));
    }

    @Transactional
    public void handleEvent(KafkaEvent event) {
        if (STUDY_CREATED.equals(event.eventType())) {
            createStudyChat((StudyEvent) event);
            return;
        }
        if (STUDY_DELETED.equals(event.eventType())) {
            removeStudyChat((StudyEvent) event);
            return;
        }
        Chat chat = chatRepository.findByStudyId(event.data().studyId())
            .orElseThrow(() -> new BadParameter("존재하지 않는 채팅방입니다"));
        publishChat(event, chat);
    }

    private void createStudyChat(StudyEvent event) {
        if (chatRepository.findByStudyId(event.data().studyId()).isPresent())
            throw new BadParameter("이미 채팅방이 존재합니다");
        chatRepository.save(Chat.builder().studyId(event.data().studyId()).build());
    }

    private void removeStudyChat(StudyEvent event) {
        Chat chat = chatRepository.findByStudyId(event.data().studyId())
            .orElseThrow(() -> new BadParameter("존재하지 않는 채팅방입니다"));
        chatMessageRepository.deleteChatMessagesByStudyChat(chat);
        chatRepository.delete(chat);
    }

    private void publishChat(KafkaEvent event, Chat chat) {
        ChatMessage eventChatMessage = event.toChatMessage(chat);
        if (eventChatMessage != null) {
            ChatMessage chatMessage = chatMessageRepository.save(eventChatMessage);
            applicationEventPublisher.publishEvent(event.kafkaEventToChatEvent(chatMessage));
        }
    }

    @Transactional(readOnly = true)
    public ChatHistoryResponse getHistory(Long studyId, LocalDateTime threshold, int amount) {
        Chat chat = chatRepository.findByStudyId(studyId)
            .orElseThrow(() -> new BadParameter("존재하지 않는 채팅방입니다"));
        Slice<ChatMessage> chatMessageSlice = chatMessageRepository.getChatMessagesByStudyChatAndCreatedAtIsLessThan(
            chat,
            threshold,
            PageRequest.of(0, amount, Sort.Direction.DESC, "createdAt")
        );
        return ChatHistoryResponse.toDto(chat.getId(), chatMessageSlice, threshold);
    }
}
