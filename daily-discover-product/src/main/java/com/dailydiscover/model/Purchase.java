package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("purchases")
public class Purchase {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("quantity")
    private Integer quantity;
    
    @TableField("unit_price")
    private BigDecimal unitPrice;
    
    @TableField("total_amount")
    private BigDecimal totalAmount;
    
    @TableField("purchase_date")
    private LocalDateTime purchaseDate;
    
    @TableField("supplier_id")
    private Long supplierId;
    
    @TableField("status")
    private String status;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}