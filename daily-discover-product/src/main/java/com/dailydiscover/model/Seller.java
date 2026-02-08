package com.dailydiscover.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Seller {
    private Long id;
    
    public void setId(Long id) {
        this.id = id;
    }
    private String name;
    private String description;
    private String logoUrl;
    private String coverUrl;
    private BigDecimal rating;
    private String responseTime;
    private String deliveryTime;
    private Integer followersCount;
    private Integer totalProducts;
    private Integer monthlySales;
    private Boolean isVerified;
    private Boolean isPremium;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}