package com.dailydiscover.recommendation.dto;

import lombok.Data;

/**
 * 个性化发现流推荐响应DTO
 */
@Data
public class PersonalizedDiscoveryResponseDTO {
    
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
     * 推荐分数
     */
    private Double recommendationScore;
    
    /**
     * 商品标语
     */
    private String goodsSlogan;
    
    /**
     * 推荐类型
     */
    private String recommendationType;
    
    /**
     * 商品价格
     */
    private Double price;
    
    /**
     * 商品原价
     */
    private Double originalPrice;
}