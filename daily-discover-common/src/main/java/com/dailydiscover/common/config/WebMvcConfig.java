package com.dailydiscover.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 * 用于注册拦截器等Web相关配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    // 不再需要RequestLoggingInterceptor，因为日志功能已通过AOP统一处理
}