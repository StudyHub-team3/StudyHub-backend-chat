package com.studyhub.study_chat.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity(name = "study_chat")
@EntityListeners(AuditingEntityListener.class)
public class Chat {
    @Id
    @Column(name = "study_chat_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "study_id", nullable = false)
    private String studyId;
}
