package com.dailydiscover.recommendation.dto;

import lombok.Data;

/**
 * 推荐服务专用商品DTO
 * 避免直接依赖产品服务的ProductBasicInfoDTO，实现服务间解耦
 */
@Data
public class RecommendationProductDTO {
    
    /**
     * 商品ID
     */
    private Long id;
    
    /**
     * 商品标题
     */
    private String title;
    
    /**
     * 商品图片URL
     */
    private String imageUrl;
    
    /**
     * 商品价格
     */
    private Double price;
    
    /**
     * 商品原价
     */
    private Double originalPrice;
    
    /**
     * 商品标语
     */
    private String goodsSlogan;
    
    /**
     * 商品描述
     */
    private String description;
    
    /**
     * 销量
     */
    private Integer salesCount;
    
    /**
     * 浏览量
     */
    private Integer viewCount;
    
    /**
     * 评分
     */
    private Double rating;
    
    /**
     * 评论数
     */
    private Integer reviews;
    
    /**
     * 商品分类
     */
    private String category;
    
    /**
     * 推荐分数
     */
    private Double recommendationScore;
    
    /**
     * 推荐类型
     */
    private String recommendationType;
}