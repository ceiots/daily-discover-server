package com.example.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Tomcat服务器配置类
 * 用于解决文件上传大小限制问题
 */
@Configuration
public class TomcatConfig {
    
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return (factory) -> {
            factory.addConnectorCustomizers(connector -> {
                // 设置POST请求大小限制为10MB
                connector.setMaxPostSize(10 * 1024 * 1024);
                // 不同版本的Tomcat可能不支持setMaxHttpHeaderSize方法
                // connector.setMaxHttpHeaderSize(8192);
            });
        };
    }
} 