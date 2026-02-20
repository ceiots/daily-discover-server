package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user_behavior_logs_core")
public class UserBehaviorLogCore {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("behavior_type")
    private String behaviorType;
    
    @TableField("behavior_weight")
    private BigDecimal behaviorWeight;
    
    @TableField("session_id")
    private String sessionId;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}