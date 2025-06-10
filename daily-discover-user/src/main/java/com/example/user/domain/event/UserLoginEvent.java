package com.example.user.domain.event;

import com.example.user.domain.model.id.UserId;
import lombok.Getter;

/**
 * 用户登录事件
 */
@Getter
public class UserLoginEvent extends DomainEvent {
    /**
     * 用户ID
     */
    private final UserId userId;

    /**
     * 用户名
     */
    private final String username;

    /**
     * 登录方式：1-用户名，2-手机号，3-邮箱
     */
    private final Integer loginType;

    /**
     * 登录IP
     */
    private final String loginIp;

    /**
     * 设备类型
     */
    private final String deviceType;

    /**
     * 构造函数
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param loginType 登录方式
     * @param loginIp 登录IP
     * @param deviceType 设备类型
     */
    public UserLoginEvent(UserId userId, String username, Integer loginType, String loginIp, String deviceType) {
        super();
        this.userId = userId;
        this.username = username;
        this.loginType = loginType;
        this.loginIp = loginIp;
        this.deviceType = deviceType;
    }
} 