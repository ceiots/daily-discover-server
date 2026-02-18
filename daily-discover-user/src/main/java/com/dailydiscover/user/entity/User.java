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
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 密码（加密存储）
     */
    @TableField("password")
    private String password;

    /**
     * 会员类型
     */
    @TableField("membership")
    private String membership;

    /**
     * 用户状态：ACTIVE, INACTIVE, BANNED, DELETED
     */
    @TableField("status")
    private String status;

    /**
     * 真实姓名
     */
    @TableField("real_name")
    private String realName;

    /**
     * 性别：MALE, FEMALE, OTHER, UNKNOWN
     */
    @TableField("gender")
    private String gender;

    /**
     * 生日
     */
    @TableField("birthday")
    private LocalDateTime birthday;

    /**
     * 头像URL
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 个人简介
     */
    @TableField("bio")
    private String bio;

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

    /**
     * 用户状态枚举
     */
    public enum Status {
        ACTIVE("ACTIVE"),
        INACTIVE("INACTIVE"),
        BANNED("BANNED"),
        DELETED("DELETED");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 性别枚举
     */
    public enum Gender {
        MALE("MALE"),
        FEMALE("FEMALE"),
        OTHER("OTHER"),
        UNKNOWN("UNKNOWN");

        private final String value;

        Gender(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}