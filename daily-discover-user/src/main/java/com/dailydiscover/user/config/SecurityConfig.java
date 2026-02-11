package com.dailydiscover.user.config;

import com.dailydiscover.common.security.SimpleSecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 用户服务安全配置
 * 认证中心职责：只负责颁发Token，安全配置由common统一管理
 */
@Configuration
public class SecurityConfig extends SimpleSecurityConfig {

    /**
     * 密码编码器配置 - 用户服务特有的业务配置
     */
    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}