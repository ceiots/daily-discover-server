package com.example.user.domain.event;

import com.example.user.domain.model.id.UserId;
import lombok.Getter;

/**
 * 用户注册事件
 * 在用户成功注册后触发，可用于后续的账户创建、积分发放等操作
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
     * 注册IP
     */
    private final String registerIp;

    /**
     * 构造函数
     *
     * @param userId     用户ID
     * @param username   用户名
     * @param registerIp 注册IP
     */
    public UserRegisteredEvent(UserId userId, String username, String registerIp) {
        super();
        this.userId = userId;
        this.username = username;
        this.registerIp = registerIp;
    }
    
    /**
     * 获取事件类型
     *
     * @return 事件类型
     */
    @Override
    public String getEventType() {
        return "USER_REGISTERED";
    }
} 