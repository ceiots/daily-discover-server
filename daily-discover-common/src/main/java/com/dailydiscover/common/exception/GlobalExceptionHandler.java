package com.dailydiscover.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
     * 处理其他异常 - RESTful风格：直接返回500状态码
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Void> handleException(Exception e) {
        log.error("系统异常", e);
        // 系统错误使用500 Internal Server Error状态码
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}