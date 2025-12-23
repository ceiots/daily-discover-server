package com.dailydiscover.common.annotation;

import java.lang.annotation.*;

/**
 * API日志注解
 * 用于标记需要自动记录日志的接口方法
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiLog {
    
    /**
     * 接口描述
     */
    String value() default "";
    
    /**
     * 是否记录请求参数（默认记录）
     */
    boolean logRequest() default true;
    
    /**
     * 是否记录响应结果（默认记录）
     */
    boolean logResponse() default true;
    
    /**
     * 是否记录执行时间（默认记录）
     */
    boolean logExecutionTime() default true;
    
    /**
     * 是否记录异常信息（默认记录）
     */
    boolean logException() default true;
}