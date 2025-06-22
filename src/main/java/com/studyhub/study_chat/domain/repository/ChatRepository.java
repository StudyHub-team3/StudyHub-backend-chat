package com.studyhub.study_chat.domain.repository;

import com.studyhub.study_chat.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByStudyId(Long studyId);
}
