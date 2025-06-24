package com.studyhub.study_chat.remote.study;

import com.studyhub.study_chat.common.dto.ApiResponseDto;
import com.studyhub.study_chat.remote.study.dto.InternalStudyInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyService {
    private final RemoteStudyBackendService remoteStudyBackendService;

    public CompletableFuture<InternalStudyInfoResponse> getStudyInfo(Long studyId) {
        ApiResponseDto<InternalStudyInfoResponse> infoForInternalUse = remoteStudyBackendService.getStudyInfoForInternalUse(studyId);
        if (!"OK".equals(infoForInternalUse.getCode())) {
            log.error("RemoteStudyBackendService: 스터디 정보를 불러올수 없습니다.");
            return null;
        }
        return CompletableFuture.completedFuture(infoForInternalUse.getData());
    }
}
