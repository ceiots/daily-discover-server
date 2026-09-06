package com.dailydiscover.common.util;

import com.dailydiscover.common.exception.BusinessException;
import com.dailydiscover.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.UUID;

/**
 * 匿名用户标识解析器
 * 从请求头 X-Anonymous-Id 获取匿名用户 ID
 */
@Component
public class AnonymousUserResolver implements HandlerMethodArgumentResolver {

    private final String headerName;

    public AnonymousUserResolver(String headerName) {
        this.headerName = headerName;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(String.class) 
                && parameter.hasParameterAnnotation(AnonymousId.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new BusinessException(ErrorCode.ANONYMOUS_ID_REQUIRED);
        }

        String anonymousId = request.getHeader(headerName);
        if (anonymousId == null || anonymousId.trim().isEmpty()) {
            // 生成一个临时 ID，实际项目中建议前端生成并持久化
            anonymousId = UUID.randomUUID().toString();
        }
        return anonymousId;
    }
}