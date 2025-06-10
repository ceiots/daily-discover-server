package com.example.user.domain.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 领域事件发布器
 * 负责发布领域事件到Spring事件总线
 */
@Component
public class DomainEventPublisher {

    private static ApplicationEventPublisher applicationEventPublisher;

    public DomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        DomainEventPublisher.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * 发布领域事件
     *
     * @param event 领域事件
     */
    public static void publish(DomainEvent event) {
        if (applicationEventPublisher != null) {
            applicationEventPublisher.publishEvent(event);
        }
    }
} 