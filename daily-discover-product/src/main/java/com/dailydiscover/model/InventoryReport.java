package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 库存报告模型类
 */
@Data
public class InventoryReport {
    private Long id;
    private String reportType;
    private String period;
    private LocalDateTime reportDate;
    private Integer totalItems;
    private Integer activeItems;
    private Integer lowStockItems;
    private Integer outOfStockItems;
    private Integer expiredItems;
    private Double totalInventoryValue;
    private Double averageTurnoverRate;
    private Integer totalMovements;
    private Integer stockInCount;
    private Integer stockOutCount;
    private Integer adjustmentCount;
    private Integer alertCount;
    private Integer resolvedAlerts;
    private Double stockAccuracyRate;
    private Double inventoryCoverageDays;
    private String summary;
    private String recommendations;
    private LocalDateTime createdAt;
}