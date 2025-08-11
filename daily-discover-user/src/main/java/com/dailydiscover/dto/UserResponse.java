package com.dailydiscover.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户响应DTO
 * 
 * @author Daily Discover Team
 * @since 2024-01-01
 */
@Data
public class UserResponse {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 生日
     */
    private LocalDateTime birthday;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 用户状态：0-正常，1-禁用
     */
    private Integer status;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * JWT Token（仅登录时返回）
     */
    private String token;

    /**
     * Token过期时间（仅登录时返回）
     */
    private LocalDateTime tokenExpireTime;
}