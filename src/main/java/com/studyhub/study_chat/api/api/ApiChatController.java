package com.studyhub.study_chat.api.backend;

import com.studyhub.study_chat.api.dto.ChatResponseDto.ChatHistory;
import com.studyhub.study_chat.common.dto.ApiResponseDto;
import com.studyhub.study_chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class BackendChatController {
    private final ChatService chatService;

    @GetMapping("/list/study/{studyId}")
    public ApiResponseDto<ChatHistory> getHistory(
        @PathVariable Long studyId,
        @RequestParam(required = false) LocalDateTime threshold,
        @RequestParam(required = false, defaultValue = "50") Integer amount
    ) {
        return ApiResponseDto.createOk(chatService.getHistory(studyId, threshold = threshold != null ? threshold : LocalDateTime.now(), amount));
    }
}
