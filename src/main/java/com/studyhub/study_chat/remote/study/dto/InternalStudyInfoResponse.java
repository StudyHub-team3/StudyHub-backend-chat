package com.studyhub.study_chat.remote.study.dto;

import java.time.LocalDateTime;

public record InternalStudyInfoResponse(
    Long studyId,
    String groupName,
    String category,
    String status,
    int mentorCount,
    int menteeCount,
    int maxMentor,
    int maxMentee,
    LocalDateTime createdAt
) {
}
