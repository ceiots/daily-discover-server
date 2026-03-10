package com.dailydiscover.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 个性化发现流推荐响应DTO
 */
@Data
public class PersonalizedDiscoveryResponseDTO {
    
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
     * 推荐分数
     */
    private BigDecimal recommendationScore;
    
    /**
     * 商品标语
     */
    private String goodsSlogan;
    
    /**
     * 推荐类型（personalized/general）
     */
    private String recommendationType;
}