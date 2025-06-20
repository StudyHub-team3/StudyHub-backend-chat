package com.studyhub.study_chat.api.backend;

import com.studyhub.study_chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class BackendChatController {
    private final ChatService chatService;
}
