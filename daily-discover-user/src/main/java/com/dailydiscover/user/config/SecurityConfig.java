package com.dailydiscover.user.config;

import com.dailydiscover.common.security.JwtAuthenticationFilter;
import com.dailydiscover.common.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 用户服务安全配置
 * 使用 common 模块的 JwtAuthenticationFilter 进行认证
 */
@Configuration
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            // 基础安全配置
            .csrf(csrf -> csrf.disable())
            
            // 禁用默认认证方式
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            
            // 添加 JWT 认证过滤器
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
            
            // 权限配置
            .authorizeHttpRequests(authz -> authz
                // 系统级公开接口
                .requestMatchers(
                    "/actuator/health",
                    "/swagger-ui/**", 
                    "/v3/api-docs/**",
                    "/webjars/**",
                    "/favicon.ico"
                ).permitAll()
                
                // 认证接口公开（登录、注册等）
                .requestMatchers("/user/api/auth/**").permitAll()
                
                // 用户相关接口需要认证
                .requestMatchers("/user/api/users/**").authenticated()
                
                // 其他接口需要认证
                .anyRequest().authenticated()
            )
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
