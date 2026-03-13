package com.dailydiscover.recommendation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 今日发现推荐项DTO
 * 使用snake_case命名规范匹配前端接口
 */
@Data
public class DailyDiscoveryResponseDTO {
    
    /**
     * 商品ID
     */
    @JsonProperty("item_id")
    private String itemId;
    
    /**
     * 商品类型
     */
    @JsonProperty("item_type")
    private String itemType;
    
    /**
     * 商品标题
     */
    @JsonProperty("title")
    private String title;
    
    /**
     * 商品图片URL
     */
    @JsonProperty("image_url")
    private String imageUrl;
    
    /**
     * 商品价格
     */
    @JsonProperty("price")
    private Double price;
    
    /**
     * 商品原价
     */
    @JsonProperty("original_price")
    private Double originalPrice;
    
    /**
     * 商品标语
     */
    @JsonProperty("goods_slogan")
    private String goodsSlogan;
    
    /**
     * 商品描述
     */
    @JsonProperty("description")
    private String description;
}