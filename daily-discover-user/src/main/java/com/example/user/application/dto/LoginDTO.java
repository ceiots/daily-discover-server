package com.example.user.application.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 登录数据传输对象
 */
@Data
@Accessors(chain = true)
public class LoginDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户名/手机号/邮箱
     */
    private String username;

    /**
     * 密码
     */
    private String password;

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
    
    /**
     * 登录IP
     */
    private String loginIp;
} 