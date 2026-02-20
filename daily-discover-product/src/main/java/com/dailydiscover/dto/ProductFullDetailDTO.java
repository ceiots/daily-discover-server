package com.dailydiscover.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 完整商品详情数据传输对象
 * 聚合所有商品相关信息，供前端商品详情页面使用
 */
@Data
public class ProductFullDetailDTO {
    
    // 商品基础信息
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;
    private String brand;
    private String model;
    
    // 价格信息
    private BigDecimal price;
    private BigDecimal currentPrice;
    private BigDecimal originalPrice;
    private Integer discount;
    
    // 评分和销量
    private BigDecimal rating;
    private Integer reviewCount;
    private Integer totalSales;
    private Integer monthlySales;
    
    // 状态和标签
    private Boolean isNew;
    private Boolean isHot;
    private Boolean isRecommended;
    private List<String> tags;
    private Integer status;
    
    // 商家信息
    private Long sellerId;
    private String sellerName;
    private BigDecimal sellerRating;
    private String sellerLogoUrl;
    
    // 时间信息
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 商品详情图片
    private List<ProductImageDTO> productImages;
    
    // 商品规格
    private List<ProductSpecDTO> specifications;
    
    // 商品特性
    private List<String> features;
    
    // 使用说明
    private List<String> usageInstructions;
    
    // 注意事项
    private List<String> precautions;
    
    // 包装内容
    private List<String> packageContents;
    
    // 相关商品
    private List<RelatedProductDTO> relatedProducts;
    
    /**
     * 商品图片DTO
     */
    @Data
    public static class ProductImageDTO {
        private Long id;
        private String imageUrl;
        private String imageType; // main, detail, scene, multi-angle
        private Integer sortOrder;
        private String thumbnailUrl;
    }
    
    /**
     * 商品规格DTO
     */
    @Data
    public static class ProductSpecDTO {
        private Long id;
        private String specName;
        private String specValue;
        private String specUnit;
        private Integer sortOrder;
    }
    
    /**
     * 相关商品DTO
     */
    @Data
    public static class RelatedProductDTO {
        private Long id;
        private String name;
        private String image;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private Integer discount;
        private BigDecimal rating;
        private Integer sales;
        private String category;
        private Integer reviewCount;
        private Boolean isNew;
        private List<String> tags;
        private LocalDateTime createdAt;
        private Double similarity;
    }
}