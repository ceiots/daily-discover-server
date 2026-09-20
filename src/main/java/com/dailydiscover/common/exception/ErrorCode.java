package com.dailydiscover.common.exception;

/**
 * 核心错误码定义（依据 03 API-MVP.md 第 15 节）
 */
public enum ErrorCode {
    SUCCESS(0, "success"),
    INVALID_PARAMETER(4000, "invalid parameter"),
    DISCOVERY_NOT_FOUND(4001, "discovery not found"),
    DISCOVERY_NOT_AVAILABLE(4002, "discovery not available"),
    INVALID_BEHAVIOR(4003, "invalid behavior"),
    INTERNAL_ERROR(5000, "internal error");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
}
