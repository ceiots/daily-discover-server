package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 评价点赞表
 */
@Data
@TableName("review_likes")
public class ReviewLike {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("review_id")
    private Long reviewId;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}