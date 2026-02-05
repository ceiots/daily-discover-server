package com.dailydiscover.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TimeBasedProduct {
    private Long id;
    private Long productId;
    private LocalDate date;
    private String timeDimension;
    private Integer rank;
    private Integer yesterdayRank;
    private Integer yesterdaySales;
    private Integer weekSales;
    private Integer monthSales;
    private Integer quarterSales;
    private Integer yearSales;
    private BigDecimal salesGrowthRate;
    private Integer viewCount;
    private Integer favoriteCount;
    private Integer cartCount;
    private BigDecimal conversionRate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}