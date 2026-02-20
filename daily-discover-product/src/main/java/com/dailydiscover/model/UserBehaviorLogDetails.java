package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_behavior_logs_details")
public class UserBehaviorLogDetails {
    
    @TableId(value = "id")
    private Long id;
    
    @TableField("referrer_url")
    private String referrerUrl;
    
    @TableField("behavior_context")
    private String behaviorContext;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}