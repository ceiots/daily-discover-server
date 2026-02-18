package com.dailydiscover.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息响应DTO
 */
@Data
public class UserResponse {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 性别
     */
    private String gender;

    /**
     * 生日
     */
    private LocalDateTime birthday;

    /**
     * 会员类型
     */
    private String membership;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}