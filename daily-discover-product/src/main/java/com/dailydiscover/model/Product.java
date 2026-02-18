package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品基础信息表（SPU - 标准化产品单元）
 */
@Data
@TableName("products")
public class Product {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("seller_id")
    private Long sellerId;
    
    @TableField("title")
    private String title;
    
    @TableField("category_id")
    private Long categoryId;
    
    @TableField("brand")
    private String brand;
    
    @TableField("model")
    private String model;
    
    @TableField("min_price")
    private BigDecimal minPrice;
    
    @TableField("max_price")
    private BigDecimal maxPrice;
    
    @TableField("status")
    private Integer status;
    
    @TableField("is_deleted")
    private Integer isDeleted;
    
    @TableField("main_image_url")
    private String mainImageUrl;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}