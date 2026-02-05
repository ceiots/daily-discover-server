package com.dailydiscover.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductRecommendation {
    private Long id;
    private Long productId;
    private Long recommendedProductId;
    private String recommendationType;
    private BigDecimal recommendationScore;
    private Integer position;
    private String algorithmVersion;
    private Boolean isActive;
    private LocalDateTime expireAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}