package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 客服坐席表
 */
@Data
@TableName("customer_service_agents")
public class CustomerServiceAgent {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("agent_name")
    private String agentName;
    
    @TableField("agent_code")
    private String agentCode;
    
    @TableField("department")
    private String department;
    
    @TableField("status")
    private String status;
    
    @TableField("max_concurrent_chats")
    private Integer maxConcurrentChats;
    
    @TableField("current_chats")
    private Integer currentChats;
    
    @TableField("total_chats")
    private Integer totalChats;
    
    @TableField("avg_response_time")
    private Integer avgResponseTime;
    
    @TableField("satisfaction_rate")
    private BigDecimal satisfactionRate;
    
    @TableField("skills")
    private String skills;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
    
    @TableField("last_activity_at")
    private LocalDateTime lastActivityAt;
}