package com.studyhub.study_chat.remote.studyMember;

import com.studyhub.study_chat.common.dto.ApiResponseDto;
import com.studyhub.study_chat.remote.studyMember.dto.StudyMemberResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
    name = "RemoteStudyMemberApiService",
    path = "/api/study-members"
)
public interface RemoteStudyMemberApiService {
    @GetMapping("/members/{studyId}")
    ApiResponseDto<List<StudyMemberResponseDto>> getMembers(@PathVariable Long studyId);
}
