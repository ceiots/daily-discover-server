package com.example.user.application.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户授权DTO
 */
@Data
@Accessors(chain = true)
public class UserAuthDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 登录类型:username,mobile,email,weixin,qq,weibo,apple
     */
    private String identityType;

    /**
     * 标识(手机号/邮箱/用户名/第三方应用的唯一标识)
     */
    private String identifier;

    /**
     * 凭据(密码/第三方token)
     */
    private String credential;

    /**
     * 是否已验证:0-未验证,1-已验证
     */
    private Boolean verified;

    /**
     * 状态:0-禁用,1-启用
     */
    private Integer status;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 绑定时间
     */
    private LocalDateTime bindTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}