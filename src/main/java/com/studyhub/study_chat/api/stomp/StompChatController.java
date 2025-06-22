package com.studyhub.study_chat.api.stomp;

import com.studyhub.study_chat.api.dto.ChatRequestDto.ChatMessageRequest;
import com.studyhub.study_chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@MessageMapping("/chat")
@RequiredArgsConstructor
public class StompChatController {
    private final ChatService chatService;

    @MessageMapping("/{studyChatId}/send/{speakerId}")
    public void chat(
        @DestinationVariable Long studyChatId,
        @DestinationVariable Long speakerId, // TODO security 작업 완료 후 context에서 추출해서 사용할 예정
        @Payload ChatMessageRequest request
    ) {
        chatService.publishChat(studyChatId, speakerId, request);
    }
}
