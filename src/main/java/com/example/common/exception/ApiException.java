package com.example.common.exception;

import com.example.common.result.IResultCode;

/**
 * API异常
 */
public class ApiException extends BusinessException {
    
    public ApiException(IResultCode errorCode) {
        super(errorCode);
    }
    
    public ApiException(String message) {
        super(message);
    }
    
    public ApiException(IResultCode errorCode, String message) {
        super(errorCode, message);
    }
} 