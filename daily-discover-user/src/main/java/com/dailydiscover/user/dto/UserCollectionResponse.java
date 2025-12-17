package com.dailydiscover.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户收藏响应DTO
 */
@Data
public class UserCollectionResponse {

    /**
     * 收藏记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 内容类型
     */
    private String itemType;

    /**
     * 内容ID
     */
    private String itemId;

    /**
     * 标题
     */
    private String title;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 收藏时间
     */
    private LocalDateTime collectedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}