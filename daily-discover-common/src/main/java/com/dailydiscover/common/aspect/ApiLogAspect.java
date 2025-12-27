package com.dailydiscover.common.aspect;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.common.config.ApiLogConfig;
import com.dailydiscover.common.util.ApiLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * API日志切面
 * 自动记录标记了@ApiLog注解的接口方法的请求、响应、执行时间等信息
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class ApiLogAspect {
    
    private final ApiLogConfig apiLogConfig;
    
    /**
     * 定义切点：所有标记了@ApiLog注解的方法
     */
    @Pointcut("@annotation(com.dailydiscover.common.annotation.ApiLog)")
    public void apiLogPointcut() {}
    
    /**
     * 定义切点：所有Controller包下的方法
     */
    @Pointcut("execution(* com.dailydiscover..controller..*.*(..))")
    public void controllerPointcut() {}
    
    /**
     * 环绕通知：记录API调用日志
     */
    @Around("apiLogPointcut() || controllerPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 检查是否启用API日志
        if (!apiLogConfig.isEnabled()) {
            return joinPoint.proceed();
        }
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 检查是否在排除列表中
        if (isExcluded(joinPoint)) {
            return joinPoint.proceed();
        }
        
        // 获取方法上的@ApiLog注解
        ApiLog apiLog = method.getAnnotation(ApiLog.class);
        String apiDescription = apiLog != null && !apiLog.value().isEmpty() ? apiLog.value() : 
                               method.getDeclaringClass().getSimpleName() + "." + method.getName();
        
        long startTime = System.currentTimeMillis();
        Object result;
        
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            
            // 记录成功日志
            HttpServletRequest request = getCurrentRequest();
            if (request != null) {
                ApiLogger.logHttpApiCall(apiDescription, request, result, duration, true);
            }
            
            return result;
            
        } catch (Throwable throwable) {
            long duration = System.currentTimeMillis() - startTime;
            
            // 记录异常日志
            HttpServletRequest request = getCurrentRequest();
            if (request != null && throwable instanceof Exception) {
                ApiLogger.logHttpApiException(apiDescription, request, (Exception) throwable, duration);
            }
            
            throw throwable;
        }
    }
    
    /**
     * 获取当前HTTP请求
     */
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) 
                RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 检查是否在排除列表中
     */
    private boolean isExcluded(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringTypeName();
        String methodName = signature.getName();
        
        // 检查包排除
        if (!apiLogConfig.getExcludePackages().isEmpty()) {
            String[] excludePackages = apiLogConfig.getExcludePackages().split(",");
            for (String pkg : excludePackages) {
                if (className.startsWith(pkg.trim())) {
                    return true;
                }
            }
        }
        
        // 检查类排除
        if (!apiLogConfig.getExcludeClasses().isEmpty()) {
            String[] excludeClasses = apiLogConfig.getExcludeClasses().split(",");
            for (String clazz : excludeClasses) {
                if (className.equals(clazz.trim())) {
                    return true;
                }
            }
        }
        
        // 检查方法排除
        if (!apiLogConfig.getExcludeMethods().isEmpty()) {
            String[] excludeMethods = apiLogConfig.getExcludeMethods().split(",");
            for (String method : excludeMethods) {
                if (methodName.equals(method.trim())) {
                    return true;
                }
            }
        }
        
        return false;
    }
}