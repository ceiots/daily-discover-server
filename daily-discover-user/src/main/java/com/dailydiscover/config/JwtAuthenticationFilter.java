package com.dailydiscover.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT认证过滤器 - 电商系统认证实现
 * 处理前端传递的Bearer Token进行用户认证
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) 
            throws ServletException, IOException {
        
        // 获取Authorization头
        String authHeader = request.getHeader(AUTH_HEADER);
        
        // 检查是否包含Bearer Token
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String token = authHeader.substring(BEARER_PREFIX.length());
            
            try {
                // 验证Token并获取用户信息
                if (isValidToken(token)) {
                    // 从Token中解析用户信息（这里简化处理，实际应该解析JWT）
                    String username = extractUsernameFromToken(token);
                    List<SimpleGrantedAuthority> authorities = extractAuthoritiesFromToken(token);
                    
                    // 创建认证对象
                    UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                    
                    // 设置认证信息到SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // Token验证失败，清除认证信息
                SecurityContextHolder.clearContext();
                logger.debug("JWT Token验证失败: " + e.getMessage());
            }
        }
        
        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 验证Token有效性（简化实现）
     */
    private boolean isValidToken(String token) {
        // 这里应该使用JWT库验证Token签名和过期时间
        // 简化实现：检查Token格式和长度
        return token != null && token.length() > 10 && !token.contains("invalid");
    }

    /**
     * 从Token中提取用户名（简化实现）
     */
    private String extractUsernameFromToken(String token) {
        // 这里应该使用JWT库解析Token
        // 简化实现：返回模拟用户名
        return "user_" + token.hashCode();
    }

    /**
     * 从Token中提取权限信息（简化实现）
     */
    private List<SimpleGrantedAuthority> extractAuthoritiesFromToken(String token) {
        // 这里应该使用JWT库解析Token中的权限信息
        // 简化实现：返回基础权限
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }
}