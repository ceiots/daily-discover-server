package com.example.user.infrastructure.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.example.user.infrastructure.common.util.JwtTokenUtil;
import com.example.user.infrastructure.common.util.UserIdExtractor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserIdInterceptor implements HandlerInterceptor {

    @Autowired
    private UserIdExtractor userIdExtractor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 使用工具类从请求中提取用户ID
        Long userId = userIdExtractor.extractUserIdFromRequest(request);
        
        // 如果成功提取到userId，设置到请求属性中
        if (userId != null) {
            request.setAttribute("userId", userId);
        }
        
        // 无论是否获取到userId，都允许请求继续处理
        // 如果需要强制用户登录，可以在这里进行拦截
        return true;
    }
}