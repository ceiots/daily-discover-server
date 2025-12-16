package com.dailydiscover.common.auth.config;

import com.dailydiscover.common.auth.service.AuthService;
import com.dailydiscover.common.auth.service.impl.AuthServiceImpl;
import com.dailydiscover.common.auth.util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 认证配置类
 */
@Configuration
public class AuthConfig {

    /**
     * 配置JWT工具类
     */
    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }

    /**
     * 配置认证服务
     */
    @Bean
    public AuthService authService() {
        return new AuthServiceImpl(jwtUtil());
    }
}