package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class InventoryTransaction {
    private Long id;
    private Long productId;
    private Long skuId;
    private String transactionType;
    private Integer quantityChange;
    private Integer previousQuantity;
    private Integer newQuantity;
    private String referenceType;
    private Long referenceId;
    private String notes;
    private Long operatorId;
    private LocalDateTime createdAt;
}