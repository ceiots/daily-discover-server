package com.dailydiscover.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户基本信息实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("users")
public class User {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 手机号（用于登录）
     */
    @TableField("phone")
    private String phone;



    /**
     * 个人简介
     */
    @TableField("bio")
    private String bio;

    /**
     * 积分
     */
    @TableField("points")
    private Integer points;

    /**
     * 等级ID
     */
    @TableField("level_id")
    private Long levelId;

    /**
     * 会员类型
     */
    @TableField("membership")
    private String membership;

    /**
     * 头像URL
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 密码（加密存储）
     */
    @TableField("password")
    private String password;



    /**
     * 手机号是否已验证
     */
    @TableField("phone_verified")
    private Boolean phoneVerified;

    /**
     * 最后登录时间
     */
    @TableField("last_login_at")
    private LocalDateTime lastLoginAt;

    /**
     * 登录次数
     */
    @TableField("login_count")
    private Integer loginCount;

    /**
     * 用户状态：ACTIVE, INACTIVE, BANNED
     */
    @TableField("status")
    private String status;

    /**
     * 验证码
     */
    @TableField("verification_code")
    private String verificationCode;

    /**
     * 验证码过期时间
     */
    @TableField("verification_code_expires_at")
    private LocalDateTime verificationCodeExpiresAt;

    /**
     * 密码重置令牌
     */
    @TableField("reset_token")
    private String resetToken;

    /**
     * 重置令牌过期时间
     */
    @TableField("reset_token_expires_at")
    private LocalDateTime resetTokenExpiresAt;

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