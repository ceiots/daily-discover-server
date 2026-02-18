package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客服会话表
 */
@Data
@TableName("customer_service_conversations")
public class CustomerServiceConversation {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("agent_id")
    private Long agentId;
    
    @TableField("category_id")
    private Long categoryId;
    
    @TableField("session_id")
    private String sessionId;
    
    @TableField("title")
    private String title;
    
    @TableField("status")
    private String status;
    
    @TableField("priority")
    private String priority;
    
    @TableField("related_order_id")
    private Long relatedOrderId;
    
    @TableField("related_product_id")
    private Long relatedProductId;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
    
    @TableField("first_response_at")
    private LocalDateTime firstResponseAt;
    
    @TableField("resolved_at")
    private LocalDateTime resolvedAt;
    
    @TableField("closed_at")
    private LocalDateTime closedAt;
    
    @TableField("satisfaction_rating")
    private Integer satisfactionRating;
    
    @TableField("satisfaction_comment")
    private String satisfactionComment;
}