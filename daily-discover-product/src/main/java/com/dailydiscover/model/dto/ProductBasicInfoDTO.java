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
    private Long sellerId;
    private String title;
    private Long categoryId;
    private String brand;
    private String model;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String mainImageUrl;
    
    // 推荐语/产品描述
    private String recommendation;
    
    // 价格信息
    private BigDecimal currentPrice; // 现价
    private BigDecimal originalPrice; // 原价
    private BigDecimal discount; // 折扣
    
    // 销量信息
    private Integer sales; // 销量
    private String urgencyHint; // 紧迫感提示
    
    // 评价信息
    private BigDecimal rating; // 平均评分
    private Integer reviews; // 评价数量
    
    // 商家信息
    private String sellerName;
    private String sellerRating;
    
    // 时间信息
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}