package com.dailydiscover.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 今日发现推荐响应DTO
 */
@Data
public class DailyDiscoveryResponseDTO {
    
    /**
     * 商品ID
     */
    private Long itemId;
    
    /**
     * 商品类型
     */
    private String itemType;
    
    /**
     * 商品标题
     */
    private String title;
    
    /**
     * 商品图片URL
     */
    private String imageUrl;
    
    /**
     * 浏览量
     */
    private Integer viewCount;
    
    /**
     * 平均评分
     */
    private BigDecimal avgRating;
    
    /**
     * 商品标语
     */
    private String goodsSlogan;
    
    /**
     * 商品价格
     */
    private BigDecimal price;
    
    /**
     * 商品原价
     */
    private BigDecimal originalPrice;
    
    /**
     * 推荐分数
     */
    private BigDecimal recommendationScore;
}