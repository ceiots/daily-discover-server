package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_behavior_logs")
public class UserBehaviorLog {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("behavior_type")
    private String behaviorType;
    
    @TableField("behavior_context")
    private String behaviorContext;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}