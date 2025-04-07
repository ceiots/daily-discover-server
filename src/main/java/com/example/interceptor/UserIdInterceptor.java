package com.example.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserIdInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 假设从请求头中获取 userId
        String userIdHeader = request.getHeader("userId");
        if (userIdHeader != null) {
            Long userId = Long.parseLong(userIdHeader);
            request.setAttribute("userId", userId);
        }
        return true;
    }
}