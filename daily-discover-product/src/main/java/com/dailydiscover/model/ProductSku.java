package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product_skus")
public class ProductSku {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("sku_code")
    private String skuCode;
    
    @TableField("specs_json")
    private String specsJson;
    
    @TableField("specs_text")
    private String specsText;
    
    @TableField("price")
    private BigDecimal price;
    
    @TableField("stock")
    private Integer stock;
    
    @TableField("sort_order")
    private Integer sortOrder;
    
    @TableField("is_deleted")
    private Boolean isDeleted;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}