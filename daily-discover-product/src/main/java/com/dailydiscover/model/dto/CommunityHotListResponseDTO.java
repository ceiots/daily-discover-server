package com.dailydiscover.model.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 社区热榜推荐响应DTO
 */
@Data
public class CommunityHotListResponseDTO {
    
    /**
     * 商品ID
     */
    private Long itemId;
    
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
     * 平均评分
     */
    private BigDecimal avgRating;
    
    /**
     * 商品标语
     */
    private String goodsSlogan;
    
    /**
     * 排名
     */
    private Integer rank;
}