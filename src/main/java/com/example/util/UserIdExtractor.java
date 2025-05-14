package com.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户ID提取工具类
 * 用于从HTTP请求头或JWT令牌中提取用户ID
 */
@Component
public class UserIdExtractor {
    
    private static final Logger log = LoggerFactory.getLogger(UserIdExtractor.class);
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    /**
     * 从请求中提取用户ID
     * @param request HTTP请求
     * @return 用户ID，如果无法提取则返回null
     */
    public Long extractUserIdFromRequest(HttpServletRequest request) {
        // 尝试从请求头中直接获取userId
        String userIdHeader = request.getHeader("userId");
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                Long userId = Long.parseLong(userIdHeader);
                log.debug("从userId请求头中提取到用户ID: {}", userId);
                return userId;
            } catch (NumberFormatException e) {
                log.warn("无效的userId请求头: {}", userIdHeader);
                // 继续尝试从Authorization头中获取
            }
        }
        
        // 从Authorization头中获取JWT令牌
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && !authHeader.isEmpty()) {
            String token = authHeader;
            if (token.startsWith("Bearer ")) {
                token = token.substring(7); // 去掉"Bearer "前缀
            }
            Long userId = jwtTokenUtil.extractUserId(token);
            log.debug("从token中提取的userId: {}", userId);
            return userId;
        }
        
        return null;
    }
    
    /**
     * 从Authorization头或userId头中提取用户ID
     * @param token JWT令牌 (可选)
     * @param userIdHeader 用户ID请求头 (可选)
     * @return 用户ID，如果无法提取则返回null
     */
    public Long extractUserId(String token, String userIdHeader) {
        // 尝试从userId请求头中提取
        if (userIdHeader != null && !userIdHeader.isEmpty()) {
            try {
                Long userId = Long.parseLong(userIdHeader);
                log.debug("从userId请求头中提取到用户ID: {}", userId);
                return userId;
            } catch (NumberFormatException e) {
                log.warn("无效的userId请求头: {}", userIdHeader);
                // 继续尝试从token中获取
            }
        }
        
        // 尝试从JWT令牌中提取
        if (token != null && !token.isEmpty()) {
            String jwtToken = token;
            // 处理Bearer token格式
            if (jwtToken.startsWith("Bearer ")) {
                jwtToken = jwtToken.substring(7);
            }
            Long userId = jwtTokenUtil.extractUserId(jwtToken);
            log.debug("从token中提取的userId: {}", userId);
            return userId;
        }
        
        return null;
    }
} 