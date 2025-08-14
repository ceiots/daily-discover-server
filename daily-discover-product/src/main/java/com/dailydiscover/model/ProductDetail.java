package com.dailydiscover.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductDetail {
    private Long id;
    private String title;
    private BigDecimal price;
    private String originalPrice;
    private String discount;
    private List<ProductImage> images;
    private List<String> detailImages;
    private String description;
    private Integer sales;
    private Double rating;
    private Integer reviewCount;
    private List<ProductSpec> specs;
    private List<String> features;
    private List<Comment> comments;
    private List<Product> relatedProducts;
    private Integer categoryId;
    private Integer brandId;
    private String brandName;
    private Integer shopId;
    private String shopName;
    private String shopAvatar;
    private Integer stock;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    @Data
    public static class ProductImage {
        private Long id;
        private String url;
        private String specType;
        private String specValue;
        private Integer sort;
    }
    
    @Data
    public static class ProductSpec {
        private Long id;
        private String name;
        private List<SpecOption> options;
        
        @Data
        public static class SpecOption {
            private Long id;
            private String value;
            private BigDecimal price;
            private Integer stock;
        }
    }
    
    @Data
    public static class Comment {
        private Long id;
        private Long userId;
        private String userName;
        private String avatar;
        private Integer rating;
        private String content;
        private List<String> images;
        private LocalDateTime createTime;
        private String specs;
        private Boolean isAnonymous;
    }
}