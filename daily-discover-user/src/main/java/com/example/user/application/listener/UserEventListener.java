package com.example.user.application.listener;

import com.example.user.domain.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 用户事件监听器
 * 负责处理用户相关的领域事件
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    /**
     * 处理用户注册事件
     * 可以在这里执行用户注册后的一系列操作，如：
     * 1. 创建用户账户
     * 2. 发放新用户积分
     * 3. 发送欢迎邮件
     * 4. 记录用户行为日志
     *
     * @param event 用户注册事件
     */
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        log.info("用户注册成功: userId={}, username={}, ip={}", 
                event.getUserId().getValue(), 
                event.getUsername(), 
                event.getRegisterIp());
        
        // TODO: 创建用户账户
        
        // TODO: 发放新用户积分
        
        // TODO: 发送欢迎邮件
        
        // TODO: 记录用户行为日志
    }
} 