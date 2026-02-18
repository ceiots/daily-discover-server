package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评价回复表
 */
@Data
@TableName("review_replies")
public class ReviewReply {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("review_id")
    private Long reviewId;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("parent_reply_id")
    private Long parentReplyId;
    
    @TableField("content")
    private String content;
    
    @TableField("is_seller_reply")
    private Boolean isSellerReply;
    
    @TableField("like_count")
    private Integer likeCount;
    
    @TableField("status")
    private String status;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}