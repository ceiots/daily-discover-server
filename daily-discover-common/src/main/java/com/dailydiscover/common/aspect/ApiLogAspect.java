package com.dailydiscover.common.aspect;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.common.config.ApiLogConfig;
import com.dailydiscover.common.util.LogTracer;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * API日志切面
 * 自动记录标记了@ApiLog注解的接口方法的请求、响应、执行时间等信息
 * 
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
     * 定义切点：所有RestController注解的类的方法
     */
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
    public void restControllerPointcut() {}
    
    /**
     * 定义切点：所有RequestMapping注解的方法
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void requestMappingPointcut() {}
    
    /**
     * 环绕通知：记录API调用日志
     */
    @Around("apiLogPointcut() || controllerPointcut() || restControllerPointcut() || requestMappingPointcut()")
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
        
        // 如果没有@ApiLog注解，使用配置文件中的默认配置
        boolean logRequest = apiLog == null ? apiLogConfig.isLogRequest() : apiLog.logRequest();
        boolean logResponse = apiLog == null ? apiLogConfig.isLogResponse() : apiLog.logResponse();
        boolean logExecutionTime = apiLog == null ? apiLogConfig.isLogExecutionTime() : apiLog.logExecutionTime();
        boolean logException = apiLog == null ? apiLogConfig.isLogException() : apiLog.logException();
        
        String methodName = getMethodName(joinPoint);
        String apiDescription = apiLog != null && !apiLog.value().isEmpty() ? apiLog.value() : methodName;
        
        // 记录请求信息
        if (logRequest) {
            Object[] args = joinPoint.getArgs();
            Map<String, Object> requestInfo = buildRequestInfo(joinPoint, args);
            LogTracer.traceApiCall(apiDescription, requestInfo, null);
        }
        
        long startTime = System.currentTimeMillis();
        Object result = null;
        
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            
            // 记录响应信息
            if (logResponse) {
                LogTracer.traceApiCall(apiDescription + " - 响应", null, result);
            }
            
            return result;
            
        } catch (Throwable throwable) {
            // 记录异常信息
            if (logException) {
                LogTracer.traceException(methodName, getRequestParams(joinPoint), 
                    throwable instanceof Exception ? (Exception) throwable : new Exception(throwable));
            }
            throw throwable;
            
        } finally {
            // 记录执行时间
            if (logExecutionTime) {
                long endTime = System.currentTimeMillis();
                LogTracer.tracePerformance(methodName, startTime, endTime);
            }
        }
    }
    
    /**
     * 构建请求信息
     */
    private Map<String, Object> buildRequestInfo(ProceedingJoinPoint joinPoint, Object[] args) {
        Map<String, Object> requestInfo = new HashMap<>();
        
        // 添加HTTP请求信息
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) 
                RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                requestInfo.put("URL", request.getRequestURL().toString());
                requestInfo.put("HTTP Method", request.getMethod());
                requestInfo.put("IP", getClientIP(request));
                requestInfo.put("User-Agent", request.getHeader("User-Agent"));
            }
        } catch (Exception e) {
            // 忽略获取HTTP请求信息的异常
        }
        
        // 添加方法参数信息
        requestInfo.put("Method Parameters", getRequestParams(joinPoint));
        
        return requestInfo;
    }
    
    /**
     * 获取方法参数信息
     */
    private Map<String, Object> getRequestParams(ProceedingJoinPoint joinPoint) {
        Map<String, Object> params = new HashMap<>();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        
        if (parameterNames != null && parameterNames.length == args.length) {
            for (int i = 0; i < parameterNames.length; i++) {
                // 敏感信息过滤（如密码等）
                if (isSensitiveParameter(parameterNames[i])) {
                    params.put(parameterNames[i], "***");
                } else {
                    params.put(parameterNames[i], args[i]);
                }
            }
        } else {
            // 如果无法获取参数名，使用索引
            for (int i = 0; i < args.length; i++) {
                params.put("arg" + i, args[i]);
            }
        }
        
        return params;
    }
    
    /**
     * 判断是否为敏感参数
     */
    private boolean isSensitiveParameter(String parameterName) {
        String[] sensitiveKeywords = {"password", "pwd", "secret", "token", "key"};
        String lowerParamName = parameterName.toLowerCase();
        return Arrays.stream(sensitiveKeywords).anyMatch(lowerParamName::contains);
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 对于多个IP的情况，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
    
    /**
     * 获取完整的方法名
     */
    private String getMethodName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getDeclaringTypeName() + "." + signature.getName();
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