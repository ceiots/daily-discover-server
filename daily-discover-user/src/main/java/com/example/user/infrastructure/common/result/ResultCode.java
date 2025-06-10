package com.example.user.infrastructure.common.result;

/**
 * API响应码枚举
 */
public enum ResultCode {

    // 通用响应码
    SUCCESS(0, "操作成功"),
    ERROR(1, "操作失败"),
    SERVER_ERROR(500, "服务器异常"),
    
    // 用户相关响应码
    USER_NOT_FOUND(1001, "用户不存在"),
    USERNAME_EXISTS(1002, "用户名已存在"),
    MOBILE_EXISTS(1003, "手机号已存在"),
    EMAIL_EXISTS(1004, "邮箱已存在"),
    PASSWORD_ERROR(1005, "密码错误"),
    USERNAME_OR_PASSWORD_ERROR(1006, "用户名或密码错误"),
    USER_DISABLED(1007, "用户已禁用"),
    USER_LOCKED(1008, "用户已锁定"),
    PASSWORD_TOO_WEAK(1009, "密码强度不够"),
    INVALID_MOBILE_FORMAT(1010, "手机号格式不正确"),
    INVALID_EMAIL_FORMAT(1011, "邮箱格式不正确"),
    INVALID_VERIFICATION_CODE(1012, "验证码不正确"),
    USER_PROFILE_NOT_FOUND(1013, "用户详情不存在"),
    
    // 会员相关响应码
    MEMBER_NOT_FOUND(2001, "会员不存在"),
    MEMBER_ALREADY_EXISTS(2002, "会员已存在"),
    MEMBER_LEVEL_NOT_FOUND(2003, "会员等级不存在"),
    MEMBER_LEVEL_EXISTS(2004, "会员等级已存在"),
    MEMBER_LEVEL_IN_USE(2005, "会员等级正在使用中"),
    MEMBER_LEVEL_INVALID(2006, "会员等级无效"),
    MEMBER_EXPIRED(2007, "会员已过期"),
    
    // 账户相关响应码
    ACCOUNT_NOT_FOUND(3001, "账户不存在"),
    ACCOUNT_EXISTS(3002, "账户已存在"),
    BALANCE_NOT_ENOUGH(3003, "余额不足"),
    POINTS_NOT_ENOUGH(3004, "积分不足"),
    INVALID_AMOUNT(3005, "金额无效"),
    INVALID_POINTS(3006, "积分无效"),
    INVALID_GROWTH_VALUE(3007, "成长值无效"),
    ACCOUNT_FROZEN(3008, "账户已冻结");
    
    private final int code;
    private final String message;
    
    ResultCode(int code, String message) {
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