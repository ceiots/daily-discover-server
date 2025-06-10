package com.example.common.exception;

import com.example.common.result.Result;
import com.example.common.result.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        LOGGER.warn("业务异常: {}", e.getMessage());
        return Result.failed(e.getErrorCode(), e.getMessage());
    }

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(ValidationException.class)
    public Result<Void> handleValidationException(ValidationException e) {
        LOGGER.warn("参数验证异常: {}", e.getMessage());
        return Result.validateFailed(e.getMessage());
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = getBindingResultErrorMessage(bindingResult);
        LOGGER.warn("参数绑定异常: {}", message);
        return Result.validateFailed(message);
    }

    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String message = getBindingResultErrorMessage(bindingResult);
        LOGGER.warn("参数校验异常: {}", message);
        return Result.validateFailed(message);
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        LOGGER.warn("约束违反异常: {}", e.getMessage());
        return Result.validateFailed(e.getMessage());
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        LOGGER.error("系统异常: ", e);
        return Result.failed(ResultCode.SERVER_ERROR, "系统异常，请联系管理员");
    }

    /**
     * 获取绑定结果的错误消息
     */
    private String getBindingResultErrorMessage(BindingResult bindingResult) {
        FieldError fieldError = bindingResult.getFieldError();
        return fieldError != null ? fieldError.getDefaultMessage() : "参数错误";
    }
}