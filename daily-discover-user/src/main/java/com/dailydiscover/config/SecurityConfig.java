package com.dailydiscover.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security配置
 * 实现合理的认证流程：公开接口无需认证，私有接口需要JWT Token
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * 安全过滤器链配置 - 电商认证最佳实践
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（REST API不需要CSRF保护）
            .csrf(csrf -> csrf.disable())
            
            // 启用CORS（跨域资源共享）
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 配置无状态会话（JWT认证）
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // 配置请求授权 - 电商最佳实践：公开接口无需认证，私有接口需要认证
            .authorizeHttpRequests(authz -> authz
                // 公开接口 - 无需认证
                .requestMatchers(
                    // 健康检查
                    "/actuator/health",
                    "/user/api/debug/health",
                    
                    // 认证相关
                    "/user/api/auth/login",
                    "/user/api/auth/register", 
                    "/user/api/auth/refresh-token",
                    "/user/api/auth/captcha",
                    
                    // 公开信息
                    "/user/api/public/**",
                    
                    // Swagger文档（开发环境）
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    
                    // 静态资源
                    "/webjars/**",
                    "/favicon.ico"
                ).permitAll()
                
                // 私有接口 - 需要认证
                .requestMatchers(
                    // 用户个人信息
                    "/user/api/users/me",
                    "/user/api/users/profile",
                    
                    // 用户操作
                    "/user/api/users/**/favorites",
                    "/user/api/users/**/orders",
                    "/user/api/users/**/settings",
                    
                    // 管理接口（需要管理员权限）
                    "/user/api/admin/**"
                ).authenticated()
                
                // 默认规则：其他所有接口需要认证
                .anyRequest().authenticated()
            )
            
            // 配置JWT认证过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // 禁用默认登录页面（使用自定义认证接口）
            .formLogin(form -> form.disable())
            
            // 禁用HTTP Basic认证（使用JWT）
            .httpBasic(basic -> basic.disable());

        return http.build();
    }

    /**
     * CORS配置 - 支持跨域访问
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许的源（支持通配符）
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // 允许携带凭证（如cookies）
        configuration.setAllowCredentials(true);
        
        // 预检请求的缓存时间（秒）
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}