package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("product_sku_specs")
public class ProductSpec {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("spec_group")
    private String specGroup;
    
    @TableField("spec_name")
    private String specName;
    
    @TableField("spec_value")
    private String specValue;
    
    @TableField("sort_order")
    private Integer sortOrder;
    
    @TableField("is_deleted")
    private Boolean isDeleted;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}