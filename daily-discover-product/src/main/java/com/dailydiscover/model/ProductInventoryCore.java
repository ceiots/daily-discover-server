package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 库存核心表
 */
@Data
@TableName("product_inventory_core")
public class ProductInventoryCore {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("sku_id")
    private Long skuId;
    
    @TableField("warehouse_id")
    private Long warehouseId;
    
    @TableField("quantity")
    private Integer quantity;
    
    @TableField("reserved_quantity")
    private Integer reservedQuantity;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}