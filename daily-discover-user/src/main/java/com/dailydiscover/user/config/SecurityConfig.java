package com.dailydiscover.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 用户服务安全配置
 * 认证中心职责：只负责颁发Token，不处理安全认证
 * 安全认证由公共模块统一处理，避免Bean定义冲突
 */
@Configuration
public class SecurityConfig {

    /**
     * 密码编码器配置 - 用户服务特有的业务配置
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}