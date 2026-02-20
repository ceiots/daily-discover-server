package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product_details")
public class ProductDetail {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("title")
    private String title;
    
    @TableField("description")
    private String description;
    
    @TableField("category_id")
    private Long categoryId;
    
    @TableField("brand")
    private String brand;
    
    @TableField("model")
    private String model;
    
    @TableField("min_price")
    private BigDecimal minPrice;
    
    @TableField("max_price")
    private BigDecimal maxPrice;
    
    @TableField("tags")
    private String tags;
    
    @TableField("status")
    private Integer status;
    
    @TableField("seller_id")
    private Long sellerId;
    
    @TableField("media_type")
    private Integer mediaType;
    
    @TableField("media_url")
    private String mediaUrl;
    
    @TableField("is_video")
    private Integer isVideo;
    
    @TableField("thumbnail_url")
    private String thumbnailUrl;
    
    @TableField("sort_order")
    private Integer sortOrder;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}