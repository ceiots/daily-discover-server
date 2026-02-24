package com.dailydiscover.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 服务层日志切面
 * 自动记录Service层方法的调用和异常信息
 */
@Aspect
@Component
@Slf4j
public class ServiceLogAspect {
    
    /**
     * 定义切点：所有Service包下的方法
     */
    @Pointcut("execution(* com.dailydiscover.service..*.*(..))")
    public void serviceLayer() {}
    
    /**
     * 环绕通知：记录服务方法调用日志
     */
    @Around("serviceLayer()")
    public Object logServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String fullMethodName = className + "." + methodName;
        
        long startTime = System.currentTimeMillis();
        
        try {
            log.debug("调用服务方法: {}", fullMethodName);
            
            Object result = joinPoint.proceed();
            
            long executionTime = System.currentTimeMillis() - startTime;
            log.debug("服务方法 {} 执行成功 - 耗时: {}ms", fullMethodName, executionTime);
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("服务方法 {} 执行异常 - 耗时: {}ms - 异常: {}", 
                fullMethodName, executionTime, e.getMessage(), e);
            throw e;
        }
    }
}