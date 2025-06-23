package com.studyhub.study_chat.service;

import com.studyhub.study_chat.api.dto.ChatResponseDto.ChatEvent;
import com.studyhub.study_chat.event.producer.KafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChatStompService {
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final KafkaMessageProducer kafkaMessageProducer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    public void publishChat(ChatEvent chatEvent) {
        kafkaMessageProducer.sendChat(chatEvent);
    }

    public void subscribeChat(ChatEvent chatEvent) {
        simpMessagingTemplate.convertAndSend("/topic/chat/study/" + chatEvent.data().studyChatId(), chatEvent);
    }
}
