package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户评价统计表
 */
@Data
@TableName("user_review_stats")
public class UserReviewStats {
    
    @TableId(value = "review_id", type = IdType.INPUT)
    private Long reviewId;
    
    @TableField("helpful_count")
    private Integer helpfulCount;
    
    @TableField("reply_count")
    private Integer replyCount;
    
    @TableField("like_count")
    private Integer likeCount;
    
    @TableField("last_updated_at")
    private LocalDateTime lastUpdatedAt;
}