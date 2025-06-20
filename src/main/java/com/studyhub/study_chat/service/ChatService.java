package com.studyhub.study_chat.service;

import com.studyhub.study_chat.domain.repository.ChatMessageRepository;
import com.studyhub.study_chat.domain.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatMessageRepository chatMessageRepository;
}
