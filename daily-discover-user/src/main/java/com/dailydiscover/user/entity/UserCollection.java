package com.dailydiscover.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户收藏实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_collections")
public class UserCollection {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 内容类型
     */
    @TableField("item_type")
    private String itemType;

    /**
     * 内容ID
     */
    @TableField("item_id")
    private String itemId;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 图片URL
     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * 价格
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 原价
     */
    @TableField("original_price")
    private BigDecimal originalPrice;

    /**
     * 收藏时间
     */
    @TableField("collected_at")
    private LocalDateTime collectedAt;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
}