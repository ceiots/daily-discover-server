package com.example.user.domain.service;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.UserDevice;
import com.example.user.domain.model.id.UserId;

import java.util.List;
import java.util.Optional;

/**
 * 用户设备领域服务接口
 */
public interface UserDeviceDomainService extends BaseDomainService {

    /**
     * 获取用户设备
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 用户设备可选值
     */
    Optional<UserDevice> getUserDevice(UserId userId, String deviceId);

    /**
     * 获取用户设备列表
     *
     * @param userId 用户ID
     * @return 用户设备列表
     */
    List<UserDevice> getUserDevices(UserId userId);

    /**
     * 分页获取用户设备
     *
     * @param userId 用户ID
     * @param pageRequest 分页请求
     * @return 用户设备分页结果
     */
    PageResult<UserDevice> getUserDevicePage(UserId userId, PageRequest pageRequest);

    /**
     * 注册设备
     *
     * @param userDevice 用户设备
     * @return 保存后的用户设备
     */
    UserDevice registerDevice(UserDevice userDevice);

    /**
     * 更新设备信息
     *
     * @param userDevice 用户设备
     * @return 更新后的用户设备
     */
    UserDevice updateDevice(UserDevice userDevice);

    /**
     * 更新推送Token
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param pushToken 推送Token
     * @return 更新后的用户设备
     */
    UserDevice updatePushToken(UserId userId, String deviceId, String pushToken);

    /**
     * 禁用设备
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否禁用成功
     */
    boolean disableDevice(UserId userId, String deviceId);

    /**
     * 启用设备
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否启用成功
     */
    boolean enableDevice(UserId userId, String deviceId);

    /**
     * 删除设备
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否删除成功
     */
    boolean deleteDevice(UserId userId, String deviceId);
}