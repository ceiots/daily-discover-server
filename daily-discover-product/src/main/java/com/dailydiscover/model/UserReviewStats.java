package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户评价统计表
 */
@Data
@TableName("user_review_stats")
public class UserReviewStats {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("total_reviews")
    private Integer totalReviews;
    
    @TableField("helpful_reviews")
    private Integer helpfulReviews;
    
    @TableField("unhelpful_reviews")
    private Integer unhelpfulReviews;
    
    @TableField("average_rating")
    private BigDecimal averageRating;
    
    @TableField("helpful_count")
    private Integer helpfulCount;
    
    @TableField("reply_count")
    private Integer replyCount;
    
    @TableField("like_count")
    private Integer likeCount;
    
    @TableField("last_updated_at")
    private LocalDateTime lastUpdatedAt;
}