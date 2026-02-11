package com.dailydiscover.user.config;

import com.dailydiscover.common.security.SimpleSecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 用户服务安全配置
 * 认证中心双重角色：
 * 1. 认证提供者：公开认证接口（登录、注册、刷新Token）
 * 2. 业务服务：保护用户相关接口（个人中心、用户信息等）
 */
@Configuration
public class SecurityConfig extends SimpleSecurityConfig {

    /**
     * 重写安全配置链，支持用户服务的双重角色
     */
    @Bean
    @Primary
    @Override
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return configureBaseSecurity(http)
            .authorizeHttpRequests(authz -> authz
                // 认证接口公开（认证提供者角色）
                .requestMatchers("/user/api/auth/**").permitAll()
                
                // 用户接口需要认证（业务服务角色）
                .requestMatchers("/user/api/users/**").authenticated()
                
                // 其他接口默认规则
                .anyRequest().authenticated()
            )
            .build();
    }

    /**
     * 密码编码器配置 - 用户服务特有的业务配置
     */
    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}