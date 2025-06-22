package com.studyhub.study_chat.event.producer;

import com.studyhub.study_chat.api.dto.ChatResponseDto.ChatEvent;
import com.studyhub.study_chat.event.event.Topic;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaMessageProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String topic, Object message) {
        kafkaTemplate.send(topic, message);
    }

    public void sendChat(ChatEvent chatEvent) {
        kafkaTemplate.send(Topic.CHAT, chatEvent);
    }
}
