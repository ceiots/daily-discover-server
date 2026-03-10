package com.dailydiscover.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

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
     * 浏览量
     */
    @JsonProperty("view_count")
    private Integer viewCount;
    
    /**
     * 平均评分
     */
    @JsonProperty("avg_rating")
    private BigDecimal avgRating;
    
    /**
     * 商品标语
     */
    @JsonProperty("goods_slogan")
    private String goodsSlogan;
    
    /**
     * 商品价格
     */
    @JsonProperty("price")
    private BigDecimal price;
    
    /**
     * 商品原价
     */
    @JsonProperty("original_price")
    private BigDecimal originalPrice;
    
    /**
     * 推荐分数
     */
    @JsonProperty("recommendation_score")
    private BigDecimal recommendationScore;
}