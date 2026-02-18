package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 库存调拨模型类
 */
@Data
public class InventoryTransfer {
    private Long id;
    private Long itemId;
    private String transferNumber;
    private Long fromWarehouseId;
    private String fromWarehouseName;
    private Long toWarehouseId;
    private String toWarehouseName;
    private Integer quantity;
    private String transferReason;
    private String transferStatus;
    private LocalDateTime transferDate;
    private LocalDateTime expectedArrivalDate;
    private LocalDateTime actualArrivalDate;
    private String trackingNumber;
    private String carrier;
    private Double shippingCost;
    private String notes;
    private Long initiatedBy;
    private String initiatedByName;
    private Long receivedBy;
    private String receivedByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}