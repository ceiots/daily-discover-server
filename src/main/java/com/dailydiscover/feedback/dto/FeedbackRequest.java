package com.dailydiscover.feedback.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeedbackRequest {
    @NotNull(message = "内容ID不能为空")
    private Long contentId;

    @NotNull(message = "反馈类型不能为空")
    private String feedbackType;

    private String reason;
}