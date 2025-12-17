package com.dailydiscover.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 验证码实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("verification_codes")
public class VerificationCode {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（可为空，用于注册场景）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 验证码
     */
    @TableField("code")
    private String code;

    /**
     * 验证码类型：LOGIN, REGISTER, RESET_PASSWORD, CHANGE_EMAIL, CHANGE_PHONE
     */
    @TableField("type")
    private String type;

    /**
     * 是否已使用
     */
    @TableField("used")
    private Boolean used;

    /**
     * 过期时间
     */
    @TableField("expires_at")
    private LocalDateTime expiresAt;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
}