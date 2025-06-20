package com.studyhub.study_chat.event.consumer;

import com.studyhub.study_chat.event.event.ChatEvent;
import com.studyhub.study_chat.event.event.Topic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMessageConsumer {
    @KafkaListener(topics = Topic.CHAT)
    void handleSiteUserInfoEvent(ChatEvent event, Acknowledgment ack) {
        log.info("event arrived : {}", event.toString());
        ack.acknowledge();
    }
}
