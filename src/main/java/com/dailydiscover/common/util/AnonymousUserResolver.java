package com.dailydiscover.common.util;

import com.dailydiscover.common.exception.BusinessException;
import com.dailydiscover.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 从 X-Anonymous-Id 请求头解析匿名用户标识（03 API-MVP.md 第 3 节）
 */
@Component
public class AnonymousUserResolver implements HandlerMethodArgumentResolver {

    private final String headerName;

    public AnonymousUserResolver(@Value("${daily-discover.anonymous-header:X-Anonymous-Id}") String headerName) {
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
        String anonymousId = request == null ? null : request.getHeader(headerName);
        if (anonymousId == null || anonymousId.isBlank() || anonymousId.length() > 64) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, HttpStatus.BAD_REQUEST,
                    "missing or invalid header: " + headerName);
        }
        return anonymousId.trim();
    }
}
