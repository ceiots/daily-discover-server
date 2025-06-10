package com.example.user.domain.model.user;

import com.example.user.domain.model.id.UserId;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户登录日志
 */
@Getter
@Setter
public class UserLoginLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private UserId userId;

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

    /**
     * 创建成功登录日志
     *
     * @param userId     用户ID
     * @param loginIp    登录IP
     * @param loginType  登录类型
     * @param deviceType 设备类型
     * @param deviceId   设备ID
     * @return 登录日志
     */
    public static UserLoginLog createSuccessLog(UserId userId, String loginIp, Integer loginType,
                                              Integer deviceType, String deviceId) {
        UserLoginLog log = new UserLoginLog();
        log.setUserId(userId);
        log.setLoginTime(LocalDateTime.now());
        log.setLoginIp(loginIp);
        log.setLoginType(loginType);
        log.setDeviceType(deviceType);
        log.setDeviceId(deviceId);
        log.setStatus(1); // 成功
        log.setCreateTime(LocalDateTime.now());
        return log;
    }

    /**
     * 创建失败登录日志
     *
     * @param userId     用户ID
     * @param loginIp    登录IP
     * @param loginType  登录类型
     * @param deviceType 设备类型
     * @param deviceId   设备ID
     * @param remark     失败原因
     * @return 登录日志
     */
    public static UserLoginLog createFailureLog(UserId userId, String loginIp, Integer loginType,
                                              Integer deviceType, String deviceId, String remark) {
        UserLoginLog log = new UserLoginLog();
        log.setUserId(userId);
        log.setLoginTime(LocalDateTime.now());
        log.setLoginIp(loginIp);
        log.setLoginType(loginType);
        log.setDeviceType(deviceType);
        log.setDeviceId(deviceId);
        log.setStatus(0); // 失败
        log.setRemark(remark);
        log.setCreateTime(LocalDateTime.now());
        return log;
    }
}