package com.example.model;

import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * 内容实体类，用于存储用户创建的图文内容
 */
@Data
public class Content {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String images; // JSON格式存储图片URL数组
    private String tags; // JSON格式存储标签数组
    private Integer status; // 状态：0草稿，1已发布
    private Date createdAt;
    private Date updatedAt;
    private Integer viewCount; // 浏览次数
    private Integer likeCount; // 点赞次数
    private Integer commentCount; // 评论次数
} 