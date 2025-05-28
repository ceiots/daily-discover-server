package com.example.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.interceptor.UserIdInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    @Autowired
    private UserIdInterceptor userIdInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userIdInterceptor).addPathPatterns("/**");
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        logger.info("Adding CORS mappings with credentials");
        registry.addMapping("/**")
            .allowedOriginPatterns("*") // 通配符和allowCredentials(true)不能同时使用
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            .allowedHeaders("*")
            .exposedHeaders("Authorization", "Content-Disposition", "Content-Length")
            .maxAge(3600)  // 预检请求的缓存时间（秒）
            .allowCredentials(false); // 允许携带凭证
    }
    
    /**
     * 配置内嵌的Tomcat服务器，设置请求体大小限制
     */
    @Bean
    public ConfigurableServletWebServerFactory webServerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(connector -> {
            connector.setMaxPostSize(100 * 1024 * 1024); // 设置为100MB
            connector.setMaxSavePostSize(100 * 1024 * 1024); // 设置为100MB
        });
        return factory;
    }
}