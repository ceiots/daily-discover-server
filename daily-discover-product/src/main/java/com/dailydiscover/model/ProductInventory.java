package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductInventory {
    private Long id;
    private Long productId;
    private Long skuId;
    private Long warehouseId;
    private String locationCode;
    private Integer quantity;
    private Integer reservedQuantity;
    private Integer availableQuantity;
    private Integer safetyStock;
    private Integer minStockLevel;
    private Integer maxStockLevel;
    private LocalDateTime lastRestockDate;
    private LocalDateTime nextRestockDate;
    private String inventoryStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}