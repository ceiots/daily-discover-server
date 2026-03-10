package com.dailydiscover.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * 生活场景推荐响应DTO
 */
@Data
public class LifeScenarioResponseDTO {
    
    /**
     * 推荐商品ID列表（JSON格式）
     */
    private List<Long> recommendedProducts;
    
    /**
     * 推荐标题
     */
    private String recommendationTitle;
    
    /**
     * 推荐描述
     */
    private String recommendationDescription;
    
    /**
     * 置信度分数
     */
    private BigDecimal confidenceScore;
    
    /**
     * 场景时间类型
     */
    private String scenarioTimeType;
    
    /**
     * 场景活动类型
     */
    private String scenarioActivityType;
    
    /**
     * 场景位置类型
     */
    private String scenarioLocationType;
}