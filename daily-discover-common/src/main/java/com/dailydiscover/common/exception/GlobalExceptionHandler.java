package com.dailydiscover.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;

/**
 * 全局异常处理器 - RESTful风格
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常 - RESTful风格：直接返回422状态码
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常", e);
        // 业务错误使用422 Unprocessable Entity状态码
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
    }

    /**
     * 处理参数校验异常 - RESTful风格：直接返回400状态码
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("参数校验异常", e);
        // 参数校验错误使用400 Bad Request状态码
        return ResponseEntity.badRequest().build();
    }

    /**
     * 处理参数绑定异常 - RESTful风格：直接返回400状态码
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Void> handleBindException(BindException e) {
        log.error("参数绑定异常", e);
        // 参数绑定错误使用400 Bad Request状态码
        return ResponseEntity.badRequest().build();
    }

    /**
     * 处理约束违反异常 - RESTful风格：直接返回400状态码
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Void> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("约束违反异常", e);
        // 约束违反错误使用400 Bad Request状态码
        return ResponseEntity.badRequest().build();
    }

    /**
     * 处理HTTP方法不支持异常 - RESTful风格：直接返回405状态码
     */
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Void> handleHttpRequestMethodNotSupportedException(org.springframework.web.HttpRequestMethodNotSupportedException e) {
        log.warn("HTTP方法不支持 - 方法: {}, URI: {}", 
            e.getMethod(), 
            e.getMessage().contains("for URI") ? e.getMessage().split("for URI")[1] : "未知");
        // HTTP方法不支持使用405 Method Not Allowed状态码
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    /**
     * 处理参数类型不匹配异常 - 提供详细的错误信息
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        // 获取详细的参数信息
        String parameterName = e.getName();
        Object value = e.getValue();
        Class<?> requiredType = e.getRequiredType();
        
        log.error("参数类型转换异常 - 参数名: {}, 传入值: {}, 期望类型: {}, URL: {}", 
            parameterName, value, requiredType != null ? requiredType.getSimpleName() : "未知", 
            getCurrentRequestURI(), e);
        
        // 参数类型错误使用400 Bad Request状态码
        return ResponseEntity.badRequest().build();
    }
    
    /**
     * 处理服务层异常 - 提供更详细的定位信息
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleException(Exception e) {
        // 获取调用栈信息，识别异常来源
        StackTraceElement[] stackTrace = e.getStackTrace();
        String exceptionSource = "未知来源";
        String controllerMethod = "未知控制器";
        
        // 查找第一个控制器或服务层方法调用
        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            if (className.contains("com.dailydiscover.controller")) {
                controllerMethod = className.substring(className.lastIndexOf('.') + 1) 
                    + "." + element.getMethodName() + "() 第" + element.getLineNumber() + "行";
                exceptionSource = "Controller层";
                break;
            } else if (className.contains("com.dailydiscover.service")) {
                controllerMethod = className.substring(className.lastIndexOf('.') + 1) 
                    + "." + element.getMethodName() + "() 第" + element.getLineNumber() + "行";
                exceptionSource = "Service层";
                break;
            }
        }
        
        log.error("系统异常 - 来源: {}, 方法: {}, URL: {}, 异常: {}", 
            exceptionSource, controllerMethod, getCurrentRequestURI(), e.getMessage(), e);
        
        // 系统错误使用500 Internal Server Error状态码
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    
    /**
     * 获取当前请求的URI
     */
    private String getCurrentRequestURI() {
        try {
            jakarta.servlet.http.HttpServletRequest request = ((ServletRequestAttributes) 
                RequestContextHolder.currentRequestAttributes()).getRequest();
            return request.getRequestURI();
        } catch (Exception e) {
            return "未知URI";
        }
    }
}