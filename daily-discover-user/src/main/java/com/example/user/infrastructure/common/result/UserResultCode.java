package com.example.user.infrastructure.common.result;

import com.example.common.result.ResultCode;
import lombok.Getter;

/**
 * 用户模块结果码
 * 用户模块特定的错误码定义，继承通用结果码
 * 错误码规则：
 * 1. 用户模块错误码范围：1001-1999
 * 2. 账户相关错误码范围：1001-1099
 * 3. 用户相关错误码范围：1100-1199
 * 4. 会员相关错误码范围：1200-1299
 */
@Getter
public enum UserResultCode {

    // 账户相关错误码 1001-1099
    ACCOUNT_NOT_FOUND(1001, "账户不存在"),
    ACCOUNT_EXISTS(1002, "账户已存在"),
    ACCOUNT_FROZEN(1003, "账户已冻结"),
    BALANCE_NOT_ENOUGH(1004, "账户余额不足"),
    POINTS_NOT_ENOUGH(1005, "账户积分不足"),
    INVALID_AMOUNT(1006, "无效的金额"),
    INVALID_POINTS(1007, "无效的积分"),
    INVALID_GROWTH_VALUE(1008, "无效的成长值"),

    // 用户相关错误码 1100-1199
    USER_NOT_FOUND(1100, "用户不存在"),
    USERNAME_EXISTS(1101, "用户名已存在"),
    MOBILE_EXISTS(1102, "手机号已存在"),
    EMAIL_EXISTS(1103, "邮箱已存在"),
    PASSWORD_ERROR(1104, "密码错误"),
    PASSWORD_TOO_WEAK(1105, "密码强度不够"),
    INVALID_MOBILE_FORMAT(1106, "手机号格式不正确"),
    INVALID_EMAIL_FORMAT(1107, "邮箱格式不正确"),
    INVALID_USERNAME_FORMAT(1108, "用户名格式不正确"),
    INVALID_VERIFICATION_CODE(1109, "验证码不正确或已过期"),
    USER_DISABLED(1110, "用户已被禁用"),
    USER_LOCKED(1111, "用户已被锁定"),
    USERNAME_OR_PASSWORD_ERROR(1112, "用户名或密码错误"),
    USER_PROFILE_NOT_FOUND(1113, "用户详情不存在"),

    // 会员相关错误码 1200-1299
    MEMBER_NOT_FOUND(1200, "会员不存在"),
    MEMBER_ALREADY_EXISTS(1201, "会员已存在"),
    MEMBER_EXPIRED(1202, "会员已过期"),
    MEMBER_DISABLED(1203, "会员已被禁用"),
    MEMBER_LEVEL_NOT_FOUND(1204, "会员等级不存在"),
    MEMBER_LEVEL_EXISTS(1205, "会员等级已存在"),
    MEMBER_LEVEL_IN_USE(1206, "会员等级正在使用中"),
    MEMBER_LEVEL_INVALID(1207, "会员等级无效"),
    FREE_SHIPPING_NOT_ENOUGH(1208, "免邮次数不足"),
    FREE_RETURN_NOT_ENOUGH(1209, "免退次数不足"),
    INVALID_MONTHS(1210, "无效的月份"),
    INVALID_COUNT(1211, "无效的次数"),

    // 新增错误码
    INSUFFICIENT_BALANCE(2001, "账户余额不足"),
    INSUFFICIENT_FREEZE_AMOUNT(2002, "冻结金额不足"),
    INSUFFICIENT_POINTS(2003, "积分不足"),
    INSUFFICIENT_GROWTH(2004, "成长值不足"),
    USER_ALREADY_EXISTS(2101, "用户已存在"),
    USER_NOT_EXISTS(2102, "用户不存在"),
    ACCOUNT_LOCKED(2105, "账户已锁定"),
    VERIFY_CODE_ERROR(2106, "验证码错误"),
    VERIFY_CODE_EXPIRED(2107, "验证码已过期"),
    MOBILE_ALREADY_BOUND(2108, "手机号已绑定"),
    EMAIL_ALREADY_BOUND(2109, "邮箱已绑定"),
    THIRD_PARTY_ALREADY_BOUND(2110, "第三方账号已绑定"),
    MEMBER_LEVEL_NOT_EXISTS(2201, "会员等级不存在"),
    MEMBER_NOT_EXISTS(2202, "会员不存在"),

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误信息
     */
    private final String message;

    UserResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取错误码
     */
    public int getCode() {
        return code;
    }
    
    /**
     * 获取错误信息
     */
    public String getMessage() {
        return message;
    }
}