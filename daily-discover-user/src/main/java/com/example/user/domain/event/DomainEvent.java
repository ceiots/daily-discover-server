package com.example.user.domain.event;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 领域事件基类
 */
@Getter
public abstract class DomainEvent {
    /**
     * 事件ID
     */
    private final String eventId;

    /**
     * 事件发生时间
     */
    private final LocalDateTime occurredOn;

    /**
     * 构造函数
     */
    protected DomainEvent() {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
    }
} 