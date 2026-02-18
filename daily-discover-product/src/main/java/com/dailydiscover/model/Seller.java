package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商家基础信息表
 */
@Data
@TableName("sellers")
public class Seller {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("name")
    private String name;
    
    @TableField("description")
    private String description;
    
    @TableField("logo_url")
    private String logoUrl;
    
    @TableField("cover_url")
    private String coverUrl;
    
    @TableField("rating")
    private BigDecimal rating;
    
    @TableField("response_time")
    private String responseTime;
    
    @TableField("delivery_time")
    private String deliveryTime;
    
    @TableField("followers_count")
    private Integer followersCount;
    
    @TableField("total_products")
    private Integer totalProducts;
    
    @TableField("monthly_sales")
    private Integer monthlySales;
    
    @TableField("is_verified")
    private Boolean isVerified;
    
    @TableField("is_premium")
    private Boolean isPremium;
    
    @TableField("status")
    private String status;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}