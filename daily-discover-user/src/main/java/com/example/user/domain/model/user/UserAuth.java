package com.example.user.domain.model.user;

import com.example.user.domain.model.id.UserId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户授权信息领域模型
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAuth implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Setter
    private Long id;

    /**
     * 用户ID
     */
    private UserId userId;

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
    @Setter
    private String credential;

    /**
     * 是否已验证:0-未验证,1-已验证
     */
    @Setter
    private Boolean verified;

    /**
     * 状态:0-禁用,1-启用
     */
    @Setter
    private Integer status;

    /**
     * 过期时间
     */
    @Setter
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
    @Setter
    private LocalDateTime updateTime;

    /**
     * 构造方法
     *
     * @param userId       用户ID
     * @param identityType 登录类型
     * @param identifier   标识
     * @param credential   凭据
     */
    private UserAuth(UserId userId, String identityType, String identifier, String credential) {
        this.userId = userId;
        this.identityType = identityType;
        this.identifier = identifier;
        this.credential = credential;
        this.verified = false;
        this.status = 1;
        this.bindTime = LocalDateTime.now();
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 创建用户授权信息
     *
     * @param userId       用户ID
     * @param identityType 登录类型
     * @param identifier   标识
     * @param credential   凭据
     * @return 用户授权信息
     */
    public static UserAuth create(UserId userId, String identityType, String identifier, String credential) {
        return new UserAuth(userId, identityType, identifier, credential);
    }

    /**
     * 验证
     */
    public void verify() {
        this.verified = true;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 启用
     */
    public void enable() {
        this.status = 1;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 禁用
     */
    public void disable() {
        this.status = 0;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新凭据
     *
     * @param credential 凭据
     */
    public void updateCredential(String credential) {
        this.credential = credential;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 设置过期时间
     *
     * @param expireTime 过期时间
     */
    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 是否已过期
     *
     * @return 是否已过期
     */
    public boolean isExpired() {
        return expireTime != null && expireTime.isBefore(LocalDateTime.now());
    }

    /**
     * 是否有效
     *
     * @return 是否有效
     */
    public boolean isValid() {
        return status == 1 && verified && !isExpired();
    }
} 