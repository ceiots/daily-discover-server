package com.dailydiscover.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户浏览历史实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("browse_history")
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
     * 浏览时间
     */
    @TableField("viewed_at")
    private LocalDateTime viewedAt;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
}