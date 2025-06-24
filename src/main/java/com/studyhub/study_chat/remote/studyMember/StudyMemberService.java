package com.studyhub.study_chat.remote.studyMember;

import com.studyhub.study_chat.common.dto.ApiResponseDto;
import com.studyhub.study_chat.remote.studyMember.dto.StudyMemberResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyMemberService {
    private final RemoteStudyMemberApiService remoteStudyMemberApiService;

    public CompletableFuture<List<StudyMemberResponseDto>> getStudyMembers(Long studyId) {
        ApiResponseDto<List<StudyMemberResponseDto>> studyMemberApiServiceMembers = remoteStudyMemberApiService.getMembers(studyId);
        if (!"OK".equals(studyMemberApiServiceMembers.getCode())) {
            log.error("RemoteStudyMemberApiService: 사용자 정보를 불러올수 없습니다.");
            return null;
        }
        return CompletableFuture.completedFuture(studyMemberApiServiceMembers.getData());
    }
}
