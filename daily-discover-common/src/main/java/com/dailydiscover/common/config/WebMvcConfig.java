package com.dailydiscover.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 * 用于注册拦截器等Web相关配置
 * 仅在库模式下生效，避免与独立服务冲突
 */
@Configuration
@ConditionalOnProperty(name = "daily-discover.common.mode", havingValue = "library")
public class WebMvcConfig implements WebMvcConfigurer {
    
    // 不再需要RequestLoggingInterceptor，因为日志功能已通过AOP统一处理
    
    /**
     * 配置CORS跨域支持
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}