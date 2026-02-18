package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品评价统计表
 */
@Data
@TableName("review_stats")
public class ReviewStats {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("total_reviews")
    private Integer totalReviews;
    
    @TableField("average_rating")
    private BigDecimal averageRating;
    
    @TableField("rating_distribution")
    private String ratingDistribution;
    
    @TableField("purchased_reviews_count")
    private Integer purchasedReviewsCount;
    
    @TableField("last_30_days_reviews")
    private Integer last30DaysReviews;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}