package com.dailydiscover.recommendation.dto;

import lombok.Data;
import java.util.List;

/**
 * 生活场景推荐响应DTO（基于时间、日期、季节）
 */
@Data
public class LifeScenarioResponseDTO {
    
    /**
     * 推荐标题
     */
    private String title;
    
    /**
     * 推荐描述
     */
    private String description;
    
    /**
     * 时间段：morning(早晨)/afternoon(下午)/evening(晚上)/night(深夜)
     */
    private String timePeriod;
    
    /**
     * 日期类型：weekday(工作日)/weekend(周末)/holiday(节假日)
     */
    private String dayType;
    
    /**
     * 季节类型：spring(春季)/summer(夏季)/autumn(秋季)/winter(冬季)
     */
    private String seasonType;
    
    /**
     * 推荐商品列表
     */
    private List<Object> recommendedProducts;
}