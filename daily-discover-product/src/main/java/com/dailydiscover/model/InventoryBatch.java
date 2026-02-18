package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 库存批次模型类
 */
@Data
public class InventoryBatch {
    private Long id;
    private Long itemId;
    private String batchNumber;
    private String supplierBatch;
    private Integer quantity;
    private Integer availableQuantity;
    private LocalDateTime productionDate;
    private LocalDateTime expiryDate;
    private LocalDateTime receivedDate;
    private String supplier;
    private String qualityStatus;
    private String storageLocation;
    private String inspectionResult;
    private String inspectionNotes;
    private LocalDateTime inspectionDate;
    private String quarantineStatus;
    private LocalDateTime quarantineStartDate;
    private LocalDateTime quarantineEndDate;
    private String quarantineReason;
    private Boolean isExpired;
    private Boolean isQuarantined;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}