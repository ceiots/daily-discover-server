package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("product_details")
public class ProductDetail {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("product_id")
    private Long productId;
    
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