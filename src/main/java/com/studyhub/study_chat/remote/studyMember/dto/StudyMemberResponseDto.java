package com.studyhub.study_chat.remote.studyMember.dto;

public record StudyMemberResponseDto(
    Long id,
    Long studyId,
    String userId,
    String userName,
    String status,
    StudyRole role,
    String comment
) {
}
