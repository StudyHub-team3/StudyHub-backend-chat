package com.studyhub.study_chat.event.consumer;

import com.studyhub.study_chat.event.event.Topic;
import com.studyhub.study_chat.event.event.board.BoardEvent;
import com.studyhub.study_chat.event.event.chat.ChatEvent;
import com.studyhub.study_chat.event.event.study.StudyEvent;
import com.studyhub.study_chat.event.event.studyMember.StudyMemberEvent;
import com.studyhub.study_chat.service.ChatService;
import com.studyhub.study_chat.service.ChatStompService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMessageConsumer {
    private final ChatService chatService;
    private final ChatStompService chatStompService;

    @KafkaListener(
        topics = Topic.CHAT,
        groupId = "studychat-#{T(java.util.UUID).randomUUID().toString()}",
        properties = JsonDeserializer.VALUE_DEFAULT_TYPE + ":com.studyhub.study_chat.event.event.chat.ChatEvent"
    )
    void listenChatEvent(ChatEvent event, Acknowledgment ack) {
        log.info("chat event arrived : {}", event.toString());
        chatStompService.subscribeChat(event);
        ack.acknowledge();
    }

    @KafkaListener(
        topics = Topic.STUDY,
        groupId = "studychat",
        properties = JsonDeserializer.VALUE_DEFAULT_TYPE + ":com.studyhub.study_chat.event.event.study.StudyEvent"
    )
    void listenStudyEvent(StudyEvent event, Acknowledgment ack) {
        log.info("study event arrived : {}", event.toString());
        chatService.handleEvent(event);
        ack.acknowledge();
    }

    @KafkaListener(
        topics = Topic.STUDY_MEMBER,
        groupId = "studychat",
        properties = JsonDeserializer.VALUE_DEFAULT_TYPE + ":com.studyhub.study_chat.event.event.studyMember.StudyMemberEvent"
    )
    void listenStudyMemberEvent(StudyMemberEvent event, Acknowledgment ack) {
        log.info("study member event arrived : {}", event.toString());
        chatService.handleEvent(event);
        ack.acknowledge();
    }

    @KafkaListener(
        topics = Topic.BOARD,
        groupId = "studychat",
        properties = JsonDeserializer.VALUE_DEFAULT_TYPE + ":com.studyhub.study_chat.event.event.board.BoardEvent"
    )
    void listenStudyEvent(BoardEvent event, Acknowledgment ack) {
        log.info("board event arrived : {}", event.toString());
        chatService.handleEvent(event);
        ack.acknowledge();
    }
}
