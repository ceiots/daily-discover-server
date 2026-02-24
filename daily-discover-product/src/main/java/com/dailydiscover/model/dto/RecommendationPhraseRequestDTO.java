package com.dailydiscover.model.dto;

import lombok.Data;
import java.util.List;

/**
 * 推荐语生成请求DTO（简化版）
 */
@Data
public class RecommendationPhraseRequestDTO {
    
    private String scenarioType;
    private List<Long> productIds;
    private String recommendationTitle;
    private String recommendationDescription;
    private String recommendationMetadata; // JSON格式：{"style": "default", "ai_generated": false}
}