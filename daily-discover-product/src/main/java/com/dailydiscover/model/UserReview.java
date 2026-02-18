package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户评价表（核心信息）
 */
@Data
@TableName("user_reviews")
public class UserReview {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("order_id")
    private Long orderId;
    
    @TableField("rating")
    private Integer rating;
    
    @TableField("title")
    private String title;
    
    @TableField("is_anonymous")
    private Boolean isAnonymous;
    
    @TableField("is_verified_purchase")
    private Boolean isVerifiedPurchase;
    
    @TableField("review_date")
    private LocalDate reviewDate;
    
    @TableField("status")
    private String status;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}