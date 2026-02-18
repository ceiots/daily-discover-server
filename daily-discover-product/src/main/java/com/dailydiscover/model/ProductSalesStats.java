package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("product_sales_stats")
public class ProductSalesStats {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("time_granularity")
    private String timeGranularity;
    
    @TableField("stat_date")
    private LocalDate statDate;
    
    @TableField("rank")
    private Integer rank;
    
    @TableField("sales_count")
    private Integer salesCount;
    
    @TableField("sales_amount")
    private BigDecimal salesAmount;
    
    @TableField("sales_growth_rate")
    private BigDecimal salesGrowthRate;
    
    @TableField("view_count")
    private Integer viewCount;
    
    @TableField("favorite_count")
    private Integer favoriteCount;
    
    @TableField("cart_count")
    private Integer cartCount;
    
    @TableField("conversion_rate")
    private BigDecimal conversionRate;
    
    @TableField("is_trending")
    private Boolean isTrending;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}