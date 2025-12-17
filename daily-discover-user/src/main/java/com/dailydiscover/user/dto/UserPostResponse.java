package com.dailydiscover.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户发布内容响应DTO
 */
@Data
public class UserPostResponse {

    /**
     * 发布内容ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 发布类型
     */
    private String postType;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 点赞数
     */
    private Integer likesCount;

    /**
     * 评论数
     */
    private Integer commentsCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}