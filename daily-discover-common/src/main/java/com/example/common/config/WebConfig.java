package com.example.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.common.interceptor.UserIdInterceptor;

/**
 * Web MVC配置类
 * 
 * 主要功能：
 * 1. 配置请求拦截器
 * 2. 配置Tomcat服务器参数
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Autowired
    private UserIdInterceptor userIdInterceptor;

    /**
     * 添加拦截器
     * 用于处理用户ID验证
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        logger.info("注册UserIdInterceptor拦截器");
        registry.addInterceptor(userIdInterceptor).addPathPatterns("/**");
    }
    
    /**
     * 配置内嵌的Tomcat服务器
     * 设置请求体大小限制为100MB
     */
    @Bean
    public ConfigurableServletWebServerFactory webServerFactory() {
        logger.info("配置Tomcat服务器，设置最大请求体大小为100MB");
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(connector -> {
            connector.setMaxPostSize(100 * 1024 * 1024); // 设置为100MB
            connector.setMaxSavePostSize(100 * 1024 * 1024); // 设置为100MB
        });
        return factory;
    }
}