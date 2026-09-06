package com.dailydiscover.common.exception;

/**
 * 错误码定义
 */
public enum ErrorCode {
    // 通用错误
    SUCCESS(0, "success"),
    INVALID_PARAMETER(1001, "参数错误"),
    RESOURCE_NOT_FOUND(1002, "资源不存在"),
    BUSINESS_ERROR(1003, "业务错误"),
    INTERNAL_ERROR(5000, "服务器内部错误"),

    // 用户相关
    USER_NOT_FOUND(2001, "用户不存在"),
    ANONYMOUS_ID_REQUIRED(2002, "匿名用户标识不能为空"),

    // 内容相关
    CONTENT_NOT_FOUND(3001, "内容不存在"),
    CONTENT_NOT_PUBLISHED(3002, "内容未发布"),

    // 推荐相关
    NO_RECOMMENDATION_AVAILABLE(4001, "暂无推荐内容");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}