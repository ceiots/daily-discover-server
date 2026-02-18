package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 库存配置表
 */
@Data
@TableName("product_inventory_config")
public class ProductInventoryConfig {
    
    @TableId(value = "inventory_id", type = IdType.INPUT)
    private Long inventoryId;
    
    @TableField("inventory_name")
    private String inventoryName;
    
    @TableField("inventory_code")
    private String inventoryCode;
    
    @TableField("location_code")
    private String locationCode;
    
    @TableField("location_description")
    private String locationDescription;
    
    @TableField("safety_stock")
    private Integer safetyStock;
    
    @TableField("min_stock_level")
    private Integer minStockLevel;
    
    @TableField("max_stock_level")
    private Integer maxStockLevel;
    
    @TableField("last_restock_date")
    private LocalDateTime lastRestockDate;
    
    @TableField("next_restock_date")
    private LocalDateTime nextRestockDate;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}