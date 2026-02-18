package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("scenario_recommendations")
public class ScenarioRecommendation {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("scenario_type")
    private String scenarioType;
    
    @TableField("time_slot")
    private String timeSlot;
    
    @TableField("location_context")
    private String locationContext;
    
    @TableField("user_state")
    private String userState;
    
    @TableField("weather_conditions")
    private String weatherConditions;
    
    @TableField("recommended_products")
    private String recommendedProducts;
    
    @TableField("scenario_story")
    private String scenarioStory;
    
    @TableField("success_rate")
    private Double successRate;
    
    @TableField("avg_engagement")
    private Double avgEngagement;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}