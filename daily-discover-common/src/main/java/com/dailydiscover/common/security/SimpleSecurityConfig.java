package com.dailydiscover.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 通用安全配置基础类
 * 提供基础安全配置，各服务可继承并自定义权限规则
 */
@Configuration
@EnableWebSecurity
public class SimpleSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            // 基础安全配置
            .csrf(csrf -> csrf.disable())
            
            // 禁用默认认证方式
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            
            // 默认不做任何权限配置，由子类重写
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
            )
            .build();
    }
}
