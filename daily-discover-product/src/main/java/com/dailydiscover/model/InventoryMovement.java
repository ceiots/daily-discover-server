package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 库存流水记录模型类
 */
@Data
public class InventoryMovement {
    private Long id;
    private Long itemId;
    private String movementType;
    private Integer quantity;
    private Integer quantityBefore;
    private Integer quantityAfter;
    private String referenceNumber;
    private String referenceType;
    private Long referenceId;
    private String batchNumber;
    private String reason;
    private String notes;
    private Long operatorId;
    private String operatorName;
    private LocalDateTime movementDate;
    private String locationFrom;
    private String locationTo;
    private Double unitCost;
    private Double totalCost;
    private LocalDateTime createdAt;
}