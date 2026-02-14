package com.dailydiscover.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private Long id;
    
    public void setId(Long id) {
        this.id = id;
    }
    private Long sellerId;
    private String title;
    private String description;
    private Long categoryId;
    private String brand;
    private BigDecimal basePrice;
    private BigDecimal originalPrice;
    private BigDecimal discount;
    private BigDecimal rating;
    private Integer reviewCount;
    private Integer totalSales;
    private Integer monthlySales;
    private String status;
    private String mainImageUrl;
    private Boolean isNew;
    private Boolean isHot;
    private Boolean isRecommended;
    private String tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}