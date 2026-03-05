package com.dailydiscover.common.logging;

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
    @Pointcut("@annotation(com.dailydiscover.common.logging.ApiLog)")
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
        
        // 读取@ApiLog注解配置（实际使用所有配置属性）
        boolean logRequest = apiLog != null ? apiLog.logRequest() : true;
        boolean logResponse = apiLog != null ? apiLog.logResponse() : true;
        boolean logExecutionTime = apiLog != null ? apiLog.logExecutionTime() : true;
        boolean logException = apiLog != null ? apiLog.logException() : true;
        
        // 获取请求参数
        Object[] args = joinPoint.getArgs();
        String requestParams = formatRequestParameters(args, method);
        
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            long duration = logExecutionTime ? System.currentTimeMillis() - startTime : -1;
            
            // 记录成功日志（根据注解配置，记录所有请求）
            HttpServletRequest request = getCurrentRequest();
            if (request != null) {
                ApiLogger.logHttpApiCall(apiDescription, request, result, duration, true, logRequest, logResponse, requestParams);
            }
            
            return result;
            
        } catch (Throwable throwable) {
            long duration = System.currentTimeMillis() - startTime;
            
            // 异常请求强制记录（不参与采样）
            if (throwable instanceof Exception && logException) {
                HttpServletRequest request = getCurrentRequest();
                if (request != null) {
                    ApiLogger.logHttpApiException(apiDescription, request, (Exception) throwable, duration, requestParams);
                }
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
    
    /**
     * 格式化请求参数
     */
    private String formatRequestParameters(Object[] args, Method method) {
        if (args == null || args.length == 0) {
            return "无参数";
        }
        
        try {
            StringBuilder paramsBuilder = new StringBuilder();
            String[] parameterNames = getParameterNames(method);
            
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    paramsBuilder.append(", ");
                }
                
                String paramName = i < parameterNames.length ? parameterNames[i] : "param" + i;
                Object paramValue = args[i];
                
                // 安全格式化参数值
                String valueStr;
                if (paramValue == null) {
                    valueStr = "null";
                } else if (paramValue instanceof String) {
                    valueStr = "\"" + paramValue + "\"";
                } else if (paramValue instanceof Number || paramValue instanceof Boolean) {
                    valueStr = paramValue.toString();
                } else {
                    // 对于复杂对象，只显示类型和哈希值，防止大对象或循环引用
                    valueStr = paramValue.getClass().getSimpleName() + "@" + System.identityHashCode(paramValue);
                }
                
                paramsBuilder.append(paramName).append("=").append(valueStr);
            }
            
            return paramsBuilder.toString();
        } catch (Exception e) {
            return "参数格式化失败: " + e.getMessage();
        }
    }
    
    /**
     * 获取方法参数名称
     */
    private String[] getParameterNames(Method method) {
        try {
            // 使用反射获取参数名称（需要编译时开启-parameters参数）
            java.lang.reflect.Parameter[] parameters = method.getParameters();
            String[] names = new String[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                names[i] = parameters[i].getName();
            }
            return names;
        } catch (Exception e) {
            // 如果无法获取参数名称，返回默认名称
            String[] defaultNames = new String[method.getParameterCount()];
            for (int i = 0; i < defaultNames.length; i++) {
                defaultNames[i] = "arg" + i;
            }
            return defaultNames;
        }
    }
}