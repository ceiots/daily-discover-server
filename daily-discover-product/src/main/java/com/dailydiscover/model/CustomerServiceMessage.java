package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客服消息表
 */
@Data
@TableName("customer_service_messages")
public class CustomerServiceMessage {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("conversation_id")
    private Long conversationId;
    
    @TableField("sender_type")
    private String senderType;
    
    @TableField("sender_id")
    private Long senderId;
    
    @TableField("message_type")
    private String messageType;
    
    @TableField("content")
    private String content;
    
    @TableField("file_url")
    private String fileUrl;
    
    @TableField("file_name")
    private String fileName;
    
    @TableField("file_size")
    private Integer fileSize;
    
    @TableField("related_order_id")
    private Long relatedOrderId;
    
    @TableField("related_product_id")
    private Long relatedProductId;
    
    @TableField("is_read")
    private Boolean isRead;
    
    @TableField("read_at")
    private LocalDateTime readAt;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}