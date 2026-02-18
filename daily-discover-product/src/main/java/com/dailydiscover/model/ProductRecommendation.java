package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product_recommendations")
public class ProductRecommendation {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("recommended_product_id")
    private Long recommendedProductId;
    
    @TableField("recommendation_type")
    private String recommendationType;
    
    @TableField("recommendation_score")
    private BigDecimal recommendationScore;
    
    @TableField("position")
    private Integer position;
    
    @TableField("recommendation_context")
    private String recommendationContext;
    
    @TableField("is_active")
    private Boolean isActive;
    
    @TableField("expire_at")
    private LocalDateTime expireAt;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}