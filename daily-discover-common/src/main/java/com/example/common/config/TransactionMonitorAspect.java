package com.example.common.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Aspect
@Component
public class TransactionMonitorAspect {
    @Before("execution(* com.example.service..*.*(..))")
    public void monitorMethods(JoinPoint joinPoint) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            System.out.println("【无事务】方法调用: " + joinPoint.getSignature().toShortString());
        }
    }
}