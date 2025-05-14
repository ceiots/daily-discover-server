package com.example.interceptor;

import com.example.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserIdInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 首先尝试从请求头中直接获取userId
        String userIdHeader = request.getHeader("userId");
        if (userIdHeader != null) {
            try {
                Long userId = Long.parseLong(userIdHeader);
                request.setAttribute("userId", userId);
                return true;
            } catch (NumberFormatException e) {
                // 忽略错误，继续尝试从Authorization头中获取
            }
        }

        // 从Authorization头中获取JWT令牌
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // 去掉"Bearer "前缀
            Long userId = jwtTokenUtil.extractUserId(token);
            if (userId != null) {
                request.setAttribute("userId", userId);
            }
        }
        
        // 无论是否获取到userId，都允许请求继续处理
        // 如果需要强制用户登录，可以在这里进行拦截
        return true;
    }
}