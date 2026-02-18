package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品规格选项表
 */
@Data
@TableName("product_sku_spec_options")
public class ProductSkuSpecOption {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("spec_id")
    private Long specId;
    
    @TableField("option_value")
    private String optionValue;
    
    @TableField("option_image")
    private String optionImage;
    
    @TableField("sort_order")
    private Integer sortOrder;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}