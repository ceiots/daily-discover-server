package com.example.user.domain.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 领域事件发布器
 */
@Component
public class DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 构造函数
     *
     * @param applicationEventPublisher Spring事件发布器
     */
    public DomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * 发布领域事件
     *
     * @param event 领域事件
     */
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
} 