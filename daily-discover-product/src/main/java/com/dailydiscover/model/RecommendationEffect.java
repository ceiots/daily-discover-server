package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 推荐效果追踪表
 */
@Data
@TableName("recommendation_effects")
public class RecommendationEffect {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("recommendation_id")
    private Long recommendationId;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("impression_count")
    private Integer impressionCount;
    
    @TableField("click_count")
    private Integer clickCount;
    
    @TableField("conversion_count")
    private Integer conversionCount;
    
    @TableField("last_impressed_at")
    private LocalDateTime lastImpressedAt;
    
    @TableField("last_clicked_at")
    private LocalDateTime lastClickedAt;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
    
    @TableField("interaction_type")
    private String interactionType;
    
    @TableField("interaction_result")
    private String interactionResult;
    
    @TableField("interaction_time")
    private LocalDateTime interactionTime;
}