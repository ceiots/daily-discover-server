package com.example.model;

import java.util.Date;

import lombok.Data;

/**
 * 用户浏览历史实体类
 */
@Data
public class UserBrowsingHistory {
    private Long id;
    private Long userId;
    private Long productId;
    private Long categoryId;
    private Date browseTime;
    private Integer browseCount;
    private Date createdAt;
    private Date updatedAt;
} 