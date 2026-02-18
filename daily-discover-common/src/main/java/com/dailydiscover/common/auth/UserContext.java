package com.dailydiscover.common.auth;

import com.dailydiscover.common.security.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 用户上下文工具类 - 供所有微服务使用
 * 负责从JWT Token中获取当前用户信息
 */
@Component
public class UserContext {
    
    private final JwtUtil jwtUtil;
    
    public UserContext(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * 获取当前用户ID（可选）
     * @return 用户ID，如果未认证则返回空
     */
    public Optional<Long> getCurrentUserId() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                    .currentRequestAttributes()).getRequest();
            
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (validateToken(token)) {
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    return Optional.of(userId);
                }
            }
        } catch (Exception e) {
            // 忽略异常，返回空
        }
        
        return Optional.empty();
    }
    
    /**
     * 获取当前用户ID（必须存在）
     * @return 用户ID
     * @throws RuntimeException 如果用户未认证或Token无效
     */
    public Long getCurrentUserIdOrThrow() {
        return getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("用户未认证或Token无效"));
    }
    
    /**
     * 检查用户是否已认证
     * @return 是否已认证
     */
    public boolean isAuthenticated() {
        return getCurrentUserId().isPresent();
    }
    
    /**
     * 验证Token有效性
     * @param token JWT Token
     * @return 是否有效
     */
    private boolean validateToken(String token) {
        try {
            jwtUtil.getUserIdFromToken(token); // 尝试解析用户ID
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}