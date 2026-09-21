package com.dailydiscover.behavior.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class BehaviorRequest {

    @NotNull(message = "discoveryId is required")
    private Long discoveryId;

    @NotBlank(message = "behaviorType is required")
    @Pattern(regexp = "IMPRESSION|DETAIL_VIEW|INTERESTED|NOT_INTERESTED|SKIP|ACTION_CLICK",
            message = "behaviorType must be one of IMPRESSION, DETAIL_VIEW, INTERESTED, NOT_INTERESTED, SKIP, ACTION_CLICK")
    private String behaviorType;

    /** metadata 可选，保存到 JSONB */
    private Object metadata;
}
