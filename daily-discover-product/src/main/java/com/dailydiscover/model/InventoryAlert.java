package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 库存预警模型类
 */
@Data
public class InventoryAlert {
    private Long id;
    private Long itemId;
    private String alertType;
    private String alertLevel;
    private String description;
    private Integer currentStock;
    private Integer threshold;
    private String thresholdType;
    private Boolean isActive;
    private Boolean isAcknowledged;
    private Long acknowledgedBy;
    private LocalDateTime acknowledgedAt;
    private String actionTaken;
    private String actionNotes;
    private LocalDateTime resolvedAt;
    private String resolutionStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}