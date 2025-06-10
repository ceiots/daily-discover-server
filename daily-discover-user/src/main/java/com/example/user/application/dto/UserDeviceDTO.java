package com.example.user.application.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户设备DTO
 */
@Data
@Accessors(chain = true)
public class UserDeviceDTO implements Serializable {
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
     * 设备名称
     */
    private String deviceName;

    /**
     * 系统版本
     */
    private String osVersion;

    /**
     * APP版本
     */
    private String appVersion;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 推送Token
     */
    private String pushToken;

    /**
     * 状态:0-禁用,1-正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}