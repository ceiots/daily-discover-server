package com.dailydiscover.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 浏览历史响应DTO
 */
@Data
public class BrowseHistoryResponse {

    /**
     * 浏览记录ID
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
     * 浏览时间
     */
    private LocalDateTime viewedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}