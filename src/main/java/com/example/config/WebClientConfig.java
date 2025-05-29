package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebClient配置类
 * 
 * 主要功能：
 * 提供预配置的WebClient.Builder，用于发起HTTP请求
 * WebClient是Spring的响应式HTTP客户端，用于替代RestTemplate
 */
@Configuration
public class WebClientConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(WebClientConfig.class);

    /**
     * 创建WebClient.Builder
     * 预配置了Content-Type为application/json
     * 
     * @return 预配置的WebClient.Builder实例
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        logger.info("创建WebClient.Builder，预配置Content-Type为application/json");
        return WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }
}
