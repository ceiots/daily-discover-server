package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品规格定义表
 */
@Data
@TableName("product_sku_specs")
public class ProductSkuSpec {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("spec_name")
    private String specName;
    
    @TableField("sort_order")
    private Integer sortOrder;
    
    @TableField("is_required")
    private Boolean isRequired;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}