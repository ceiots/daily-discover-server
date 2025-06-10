package com.example.user.application.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户DTO
 */
@Data
@Accessors(chain = true)
public class UserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;

    /**
     * 状态：0-禁用，1-正常，2-锁定
     */
    private Integer status;

    /**
     * 用户类型：1-普通用户，2-商家，3-官方账号
     */
    private Integer userType;

    /**
     * 注册时间
     */
    private LocalDateTime registerTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 是否会员
     */
    private Boolean isMember;

    /**
     * 会员等级
     */
    private Integer memberLevel;

    /**
     * 会员到期时间
     */
    private LocalDateTime memberExpireTime;

    /**
     * 是否永久会员
     */
    private Boolean isForeverMember;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 成长值
     */
    private Integer growthValue;

    /**
     * 账户余额
     */
    private String balance;

    /**
     * 用户角色列表
     */
    private List<String> roles;

    /**
     * 用户权限列表
     */
    private List<String> permissions;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 