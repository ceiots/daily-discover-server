package com.dailydiscover.behavior.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BehaviorRequest {

    @NotNull(message = "discoveryId is required")
    private Long discoveryId;

    @NotBlank(message = "behaviorType is required")
    private String behaviorType;

    /** metadata 可选，保存到 JSONB */
    private Object metadata;
}
