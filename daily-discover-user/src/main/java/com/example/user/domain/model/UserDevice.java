package com.example.user.domain.model;

import com.example.user.domain.model.id.UserId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 用户设备领域模型
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDevice implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Setter
    private Long id;

    /**
     * 用户ID
     */
    private UserId userId;

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

    /**
     * 创建用户设备
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param deviceType 设备类型
     * @param deviceModel 设备型号
     * @param osVersion 系统版本
     * @param appVersion APP版本
     * @return 用户设备
     */
    public static UserDevice create(UserId userId, String deviceId, Integer deviceType, 
                                   String deviceModel, String osVersion, String appVersion) {
        UserDevice device = new UserDevice();
        device.userId = userId;
        device.deviceId = deviceId;
        device.deviceType = deviceType;
        device.deviceModel = deviceModel;
        device.osVersion = osVersion;
        device.appVersion = appVersion;
        device.lastLoginTime = LocalDateTime.now();
        device.status = 1;
        device.createTime = LocalDateTime.now();
        device.updateTime = LocalDateTime.now();
        return device;
    }

    /**
     * 设置设备名称
     *
     * @param deviceName 设备名称
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 设置推送Token
     *
     * @param pushToken 推送Token
     */
    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新最后登录时间
     */
    public void updateLastLoginTime() {
        this.lastLoginTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新APP版本
     *
     * @param appVersion APP版本
     */
    public void updateAppVersion(String appVersion) {
        this.appVersion = appVersion;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 禁用设备
     */
    public void disable() {
        this.status = 0;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 启用设备
     */
    public void enable() {
        this.status = 1;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 是否启用
     *
     * @return 是否启用
     */
    public boolean isEnabled() {
        return this.status != null && this.status == 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDevice that = (UserDevice) o;
        return Objects.equals(id, that.id) || 
               (Objects.equals(userId, that.userId) && Objects.equals(deviceId, that.deviceId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, deviceId);
    }
}