package com.example.model;

import java.util.Date;

import lombok.Data;

/**
 * 用户偏好实体类
 */
@Data
public class UserPreference {
    private Long id;
    private Long userId;
    private Long categoryId;
    private Integer preferenceLevel; // 偏好等级，1-5
    private Date createdAt;
    private Date updatedAt;
} 