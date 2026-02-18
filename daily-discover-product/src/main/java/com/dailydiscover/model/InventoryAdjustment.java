package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 库存调整模型类
 */
@Data
public class InventoryAdjustment {
    private Long id;
    private Long itemId;
    private String adjustmentType;
    private Integer quantityBefore;
    private Integer quantityAfter;
    private Integer adjustmentQuantity;
    private String reason;
    private String referenceNumber;
    private String notes;
    private Long adjustedBy;
    private String adjustedByName;
    private LocalDateTime adjustmentDate;
    private Boolean requiresApproval;
    private Long approvedBy;
    private LocalDateTime approvedAt;
    private String approvalStatus;
    private LocalDateTime createdAt;
}