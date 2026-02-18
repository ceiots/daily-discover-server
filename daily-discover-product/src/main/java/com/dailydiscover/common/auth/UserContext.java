package com.dailydiscover.common.auth;

import com.dailydiscover.common.security.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@Component
public class UserContext {
    
    private final JwtUtil jwtUtil;
    
    public UserContext(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
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
    
    public Long getCurrentUserIdOrThrow() {
        return getCurrentUserId()
                .orElseThrow(() -> new RuntimeException("用户未认证或Token无效"));
    }
    
    public boolean isAuthenticated() {
        return getCurrentUserId().isPresent();
    }
    
    private boolean validateToken(String token) {
        try {
            jwtUtil.getUserIdFromToken(token); // 尝试解析用户ID
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}