package com.studyhub.study_chat.domain.repository;

import com.studyhub.study_chat.domain.Chat;
import com.studyhub.study_chat.domain.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Slice<ChatMessage> getChatMessagesByStudyChatAndCreatedAtIsLessThan(Chat studyChat, LocalDateTime threshold, Pageable pageable);
}
