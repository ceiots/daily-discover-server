package com.example.user.domain.event;

import com.example.user.domain.model.id.UserId;
import lombok.Getter;

/**
 * 用户注册事件
 */
@Getter
public class UserRegisteredEvent extends DomainEvent {
    /**
     * 用户ID
     */
    private final UserId userId;

    /**
     * 用户名
     */
    private final String username;

    /**
     * 注册方式：1-用户名，2-手机号，3-邮箱
     */
    private final Integer registerType;

    /**
     * 构造函数
     *
     * @param userId 用户ID
     * @param username 用户名
     * @param registerType 注册方式
     */
    public UserRegisteredEvent(UserId userId, String username, Integer registerType) {
        super();
        this.userId = userId;
        this.username = username;
        this.registerType = registerType;
    }
} 