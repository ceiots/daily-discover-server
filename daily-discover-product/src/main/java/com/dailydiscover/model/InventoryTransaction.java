package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 库存操作记录表
 */
@Data
@TableName("inventory_transactions")
public class InventoryTransaction {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("inventory_id")
    private Long inventoryId;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("sku_id")
    private Long skuId;
    
    @TableField("warehouse_id")
    private Long warehouseId;
    
    @TableField("transaction_type")
    private String transactionType;
    
    @TableField("quantity_change")
    private Integer quantityChange;
    
    @TableField("previous_quantity")
    private Integer previousQuantity;
    
    @TableField("new_quantity")
    private Integer newQuantity;
    
    @TableField("reference_type")
    private String referenceType;
    
    @TableField("reference_id")
    private Long referenceId;
    
    @TableField("notes")
    private String notes;
    
    @TableField("operator_id")
    private Long operatorId;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}