package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品分类表（优化树形结构）
 */
@Data
@TableName("product_categories")
public class ProductCategory {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("parent_id")
    private Long parentId;
    
    @TableField("path")
    private String path;
    
    @TableField("name")
    private String name;
    
    @TableField("image_url")
    private String imageUrl;
    
    @TableField("sort_order")
    private Integer sortOrder;
    
    @TableField("level")
    private Integer level;
    
    @TableField("status")
    private Integer status;
    
    @TableField("is_deleted")
    private Integer isDeleted;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}