package com.dailydiscover.behavior.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BehaviorRequest {
    @NotNull(message = "内容ID不能为空")
    private Long contentId;

    @NotNull(message = "行为类型不能为空")
    private String behaviorType;
}