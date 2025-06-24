package com.studyhub.study_chat.remote.study;

import com.studyhub.study_chat.common.dto.ApiResponseDto;
import com.studyhub.study_chat.remote.study.dto.InternalStudyInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "RemoteStudyBackendService",
    path = "/backend/studies"
)
public interface RemoteStudyBackendService {
    @GetMapping("/{studyId}")
    ApiResponseDto<InternalStudyInfoResponse> getStudyInfoForInternalUse(@PathVariable Long studyId);
}