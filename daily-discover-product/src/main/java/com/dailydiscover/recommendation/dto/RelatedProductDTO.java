package com.dailydiscover.recommendation.dto;

import lombok.Data;

/**
 * 相关商品DTO
 * 用于前端相关推荐组件的数据传输
 */
@Data
public class RelatedProductDTO {
    
    /**
     * 商品ID
     */
    private String id;
    
    /**
     * 商品名称
     */
    private String name;
    
    /**
     * 商品图片URL
     */
    private String imageUrl;
    
    /**
     * 商品价格
     */
    private Double price;
    
    /**
     * 推荐类型
     */
    private String recommendationType;
    
    /**
     * 推荐分数
     */
    private Double recommendationScore;
}