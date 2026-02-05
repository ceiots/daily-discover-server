package com.dailydiscover.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductSku {
    private Long id;
    private Long productId;
    private String skuCode;
    private String skuName;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stockQuantity;
    private Integer reservedQuantity;
    private Integer availableQuantity;
    private String specCombination;
    private BigDecimal weight;
    private BigDecimal volume;
    private String barcode;
    private Boolean isDefault;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}