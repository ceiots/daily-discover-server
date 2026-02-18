package com.dailydiscover.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    private java.math.BigDecimal price;

    /**
     * 原价
     */
    @TableField("original_price")
    private java.math.BigDecimal originalPrice;

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

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 收藏类型枚举
     */
    public enum CollectionType {
        PRODUCT("product"),
        SHOP("shop"),
        ARTICLE("article");

        private final String value;

        CollectionType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 收藏状态枚举
     */
    public enum Status {
        ACTIVE("active"),
        INACTIVE("inactive");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}