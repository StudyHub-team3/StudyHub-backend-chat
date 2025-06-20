package com.studyhub.study_chat.domain.repository;

import com.studyhub.study_chat.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
