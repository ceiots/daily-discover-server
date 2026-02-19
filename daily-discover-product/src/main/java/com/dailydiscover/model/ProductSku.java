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
    
    @TableField("seller_id")
    private Long sellerId;
    
    @TableField("price")
    private BigDecimal price;
    
    @TableField("original_price")
    private BigDecimal originalPrice;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}