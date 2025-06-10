package com.example.user.application.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户登录日志DTO
 */
@Data
@Accessors(chain = true)
public class UserLoginLogDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 登录时间
     */
    private LocalDateTime loginTime;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * 登录方式:1-账号密码,2-手机验证码,3-第三方登录
     */
    private Integer loginType;

    /**
     * 设备类型:1-iOS,2-Android,3-H5,4-小程序,5-PC
     */
    private Integer deviceType;

    /**
     * 设备ID
     */
    private String deviceId;

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
     * 位置
     */
    private String location;

    /**
     * 状态:0-失败,1-成功
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}