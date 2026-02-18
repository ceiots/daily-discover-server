package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 促销活动表实体类
 */
@Data
@TableName("promotion_activities")
public class PromotionActivity {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("activity_name")
    private String activityName;
    
    @TableField("activity_type")
    private String activityType;
    
    @TableField("start_time")
    private LocalDateTime startTime;
    
    @TableField("end_time")
    private LocalDateTime endTime;
    
    @TableField("target_type")
    private String targetType;
    
    @TableField("target_ids")
    private String targetIds;
    
    @TableField("rules")
    private String rules;
    
    @TableField("description")
    private String description;
    
    @TableField("status")
    private String status;
    
    @TableField("participation_count")
    private Integer participationCount;
    
    @TableField("order_count")
    private Integer orderCount;
    
    @TableField("total_discount_amount")
    private BigDecimal totalDiscountAmount;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}