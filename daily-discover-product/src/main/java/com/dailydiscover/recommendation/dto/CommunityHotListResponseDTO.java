package com.dailydiscover.recommendation.dto;

import lombok.Data;

/**
 * 社区热榜推荐响应DTO
 */
@Data
public class CommunityHotListResponseDTO {
    
    /**
     * 商品ID
     */
    private String itemId;
    
    /**
     * 商品标题
     */
    private String title;
    
    /**
     * 商品图片URL
     */
    private String imageUrl;
    
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
     * 商品标语
     */
    private String goodsSlogan;
    
    /**
     * 商品价格
     */
    private String price;
    
    /**
     * 商品原价
     */
    private String originalPrice;
}