package com.dailydiscover.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 推荐语响应DTO（简化版）
 */
@Data
public class RecommendationPhraseResponseDTO {
    
    private Long id;
    private String scenarioType;
    private String recommendationTitle;
    private String recommendationDescription;
    private String recommendationMetadata; // JSON格式：{"style": "default", "ai_generated": false, "approval_status": "pending", "last_used_at": "2026-02-24T10:30:00"}
    private LocalDateTime createdAt;
}