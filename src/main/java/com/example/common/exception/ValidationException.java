package com.example.common.exception;

import com.example.common.result.ResultCode;

/**
 * 参数验证异常
 */
public class ValidationException extends BusinessException {
    
    public ValidationException(String message) {
        super(ResultCode.VALIDATE_FAILED, message);
    }
}