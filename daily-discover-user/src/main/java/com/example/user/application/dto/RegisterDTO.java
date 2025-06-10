package com.example.user.application.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 注册数据传输对象
 */
@Data
@Accessors(chain = true)
public class RegisterDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 确认密码
     */
    private String confirmPassword;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 验证码
     */
    private String code;

    /**
     * 验证码类型:1-手机,2-邮箱
     */
    private Integer codeType;

    /**
     * 注册IP
     */
    private String registerIp;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 设备类型:1-iOS,2-Android,3-H5,4-小程序,5-PC
     */
    private Integer deviceType;

    /**
     * 设备型号
     */
    private String deviceModel;

    /**
     * 系统版本
     */
    private String osVersion;

    /**
     * APP版本
     */
    private String appVersion;
} 