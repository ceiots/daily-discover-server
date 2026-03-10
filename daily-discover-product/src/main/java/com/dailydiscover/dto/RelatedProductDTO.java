package com.dailydiscover.dto;

import lombok.Data;
import java.math.BigDecimal;

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
    private String image;
    
    /**
     * 商品价格
     */
    private BigDecimal price;
    
    /**
     * 商品原价
     */
    private BigDecimal originalPrice;
    
    /**
     * 商品折扣
     */
    private BigDecimal discount;
    
    /**
     * 商品评分
     */
    private BigDecimal rating;
    
    /**
     * 商品销量
     */
    private Integer sales;
    
    /**
     * 商品分类
     */
    private String category;
    
    /**
     * 商品评论数
     */
    private Integer reviewCount;
    
    /**
     * 是否新品
     */
    private Boolean isNew;
    
    /**
     * 商品标签
     */
    private String[] tags;
    
    /**
     * 推荐类型
     */
    private String recommendationType;
    
    /**
     * 推荐相似度
     */
    private BigDecimal similarity;
    
    /**
     * 推荐优先级
     */
    private Integer priority;
}