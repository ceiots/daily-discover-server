package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客服分类表
 */
@Data
@TableName("customer_service_categories")
public class CustomerServiceCategory {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("category_name")
    private String categoryName;
    
    @TableField("parent_id")
    private Long parentId;
    
    @TableField("category_type")
    private String categoryType;
    
    @TableField("description")
    private String description;
    
    @TableField("sort_order")
    private Integer sortOrder;
    
    @TableField("is_enabled")
    private Boolean isEnabled;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}