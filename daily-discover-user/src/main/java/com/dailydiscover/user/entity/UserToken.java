package com.dailydiscover.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户令牌实体类（统一管理所有令牌）
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_tokens")
public class UserToken {

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
     * 令牌类型（refresh/verification/reset）
     */
    @TableField("token_type")
    private String tokenType;

    /**
     * 令牌值
     */
    @TableField("token_value")
    private String tokenValue;

    /**
     * 验证码类型（register/login/reset_password/change_phone）
     */
    @TableField("code_type")
    private String codeType;

    /**
     * 过期时间
     */
    @TableField("expires_at")
    private LocalDateTime expiresAt;

    /**
     * 是否已使用
     */
    @TableField("is_used")
    private Boolean isUsed;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 令牌类型枚举
     */
    public enum TokenType {
        REFRESH("refresh"),
        VERIFICATION("verification"),
        RESET("reset");

        private final String value;

        TokenType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 验证码类型枚举
     */
    public enum CodeType {
        REGISTER("register"),
        LOGIN("login"),
        RESET_PASSWORD("reset_password"),
        CHANGE_PHONE("change_phone");

        private final String value;

        CodeType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}