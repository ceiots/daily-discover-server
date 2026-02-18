package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 库存项模型类
 */
@Data
public class InventoryItem {
    private Long id;
    private Long productId;
    private String sku;
    private String name;
    private String description;
    private String category;
    private String brand;
    private String unit;
    private Integer currentStock;
    private Integer availableStock;
    private Integer reservedStock;
    private Integer safetyStock;
    private Integer maxStock;
    private Integer reorderPoint;
    private Double costPrice;
    private Double sellingPrice;
    private Double averageCost;
    private Double totalValue;
    private String location;
    private Long warehouseId;
    private String warehouseName;
    private String storageConditions;
    private String handlingInstructions;
    private Boolean isActive;
    private Boolean isTracked;
    private Boolean isPerishable;
    private Integer shelfLifeDays;
    private String dimensions;
    private Double weight;
    private String supplier;
    private String supplierCode;
    private String barcode;
    private String qrCode;
    private String notes;
    private LocalDateTime lastStockInDate;
    private LocalDateTime lastStockOutDate;
    private LocalDateTime lastInventoryCheck;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}