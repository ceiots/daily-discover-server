package com.dailydiscover.feedback.controller;

import com.dailydiscover.common.response.ApiResponse;
import com.dailydiscover.common.util.AnonymousId;
import com.dailydiscover.feedback.dto.FeedbackRequest;
import com.dailydiscover.feedback.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 反馈控制器
 * - POST /api/v1/feedbacks  提交反馈
 */
@Slf4j
@RestController
@RequestMapping("/v1/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * 提交用户反馈
     */
    @PostMapping
    public ApiResponse<Void> submitFeedback(
            @AnonymousId String anonymousId,
            @Valid @RequestBody FeedbackRequest request) {
        log.debug("Submit feedback, user: {}, contentId: {}, type: {}", 
                anonymousId, request.getContentId(), request.getFeedbackType());

        feedbackService.submitFeedback(
                anonymousId,
                request.getContentId(),
                request.getFeedbackType(),
                request.getReason()
        );

        return ApiResponse.success();
    }
}