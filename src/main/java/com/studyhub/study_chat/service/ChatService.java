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
import com.studyhub.study_chat.remote.study.StudyService;
import com.studyhub.study_chat.remote.study.dto.InternalStudyInfoResponse;
import com.studyhub.study_chat.remote.studyMember.StudyMemberService;
import com.studyhub.study_chat.remote.studyMember.dto.StudyMemberResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.studyhub.study_chat.event.event.study.StudyEventType.STUDY_CREATED;
import static com.studyhub.study_chat.event.event.study.StudyEventType.STUDY_DELETED;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final StudyService studyService;
    private final StudyMemberService studyMemberService;

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
        StudyMetadata studyMetaData = getStudyMetaData(studyId, threshold);
        return ChatHistoryResponse.toDto(chat.getId(), chatMessageSlice, threshold, studyMetaData.studyMembers, studyMetaData.studyInfo);
    }

    private StudyMetadata getStudyMetaData(Long studyId, LocalDateTime threshold) {
        List<StudyMemberResponseDto> studyMembers = null;
        InternalStudyInfoResponse studyInfo = null;
        if (threshold != null) {
            CompletableFuture<List<StudyMemberResponseDto>> studyMembersFuture = studyMemberService.getStudyMembers(studyId);
            CompletableFuture<InternalStudyInfoResponse> studyInfoFutre = studyService.getStudyInfo(studyId);
            CompletableFuture.allOf(studyMembersFuture, studyInfoFutre); // 작업 완료시까지 대기

            try {
                studyMembers = studyMembersFuture.get(2, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException ignore) {
                log.error("RemoteStudyMemberApiService: 사용자 정보를 불러올수 없습니다.");
            }

            try {
                studyInfo = studyInfoFutre.get(2, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException ignore) {
                log.error("RemoteStudyBackendService: 스터디 정보를 불러올수 없습니다.");
            }
        }
        return new StudyMetadata(studyMembers, studyInfo);
    }

    record StudyMetadata(
        List<StudyMemberResponseDto> studyMembers,
        InternalStudyInfoResponse studyInfo
    ) {
    }
}
