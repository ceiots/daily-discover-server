package com.dailydiscover.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户浏览历史实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_browse_history")
public class BrowseHistory {

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
     * 商品ID
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 浏览时长（秒）
     */
    @TableField("browse_duration")
    private Integer browseDuration;

    /**
     * 浏览时间
     */
    @TableField("browsed_at")
    private LocalDateTime browsedAt;

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
}