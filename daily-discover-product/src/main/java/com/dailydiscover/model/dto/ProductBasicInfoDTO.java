package com.dailydiscover.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品基础信息DTO（用于分层加载第一层数据）
 * 包含商品详情页面首屏显示所需的所有基本信息
 */
@Data
public class ProductBasicInfoDTO {
    
    // 商品基础信息
    private Long id;
    private Long seller_id;
    private String title;
    private Long category_id;
    private String brand;
    private String model;
    private BigDecimal min_price;
    private BigDecimal max_price;
    private String main_image_url;
    
    // 推荐语/产品描述
    private String base_recommendation;
    
    // 折扣信息
    private BigDecimal discount; // 折扣
    
    // 销量信息
    private Integer sales_count; // 销量
    private String urgency_hint; // 紧迫感提示
    
    // 评价信息
    private BigDecimal average_rating; // 平均评分
    private Integer total_reviews; // 评价数量
    
    // 商家信息
    private String name;
    private String rating;
    
    // 时间信息
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}