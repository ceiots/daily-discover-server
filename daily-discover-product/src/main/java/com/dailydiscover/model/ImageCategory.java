package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("image_categories")
public class ImageCategory {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("category_name")
    private String categoryName;
    
    @TableField("description")
    private String description;
    
    @TableField("sort_order")
    private Integer sortOrder;
    
    @TableField("is_active")
    private Boolean isActive;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}