package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("product_images")
public class ProductImage {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("image_url")
    private String imageUrl;
    
    @TableField("image_type")
    private String imageType;
    
    @TableField("sort_order")
    private Integer sortOrder;
    
    @TableField("is_primary")
    private Boolean isPrimary;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}