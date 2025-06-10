package com.example.user.application.service;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.application.dto.UserDeviceDTO;

import java.util.List;

/**
 * 用户设备应用服务接口
 */
public interface UserDeviceService extends BaseApplicationService {

    /**
     * 获取用户设备
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 用户设备DTO
     */
    UserDeviceDTO getUserDevice(Long userId, String deviceId);

    /**
     * 获取用户设备列表
     *
     * @param userId 用户ID
     * @return 用户设备DTO列表
     */
    List<UserDeviceDTO> getUserDevices(Long userId);

    /**
     * 分页获取用户设备
     *
     * @param userId 用户ID
     * @param pageRequest 分页请求
     * @return 用户设备分页结果
     */
    PageResult<UserDeviceDTO> getUserDevicePage(Long userId, PageRequest pageRequest);

    /**
     * 注册设备
     *
     * @param userDeviceDTO 用户设备DTO
     * @return 用户设备DTO
     */
    UserDeviceDTO registerDevice(UserDeviceDTO userDeviceDTO);

    /**
     * 更新设备信息
     *
     * @param userDeviceDTO 用户设备DTO
     * @return 用户设备DTO
     */
    UserDeviceDTO updateDevice(UserDeviceDTO userDeviceDTO);

    /**
     * 更新推送Token
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param pushToken 推送Token
     * @return 用户设备DTO
     */
    UserDeviceDTO updatePushToken(Long userId, String deviceId, String pushToken);

    /**
     * 禁用设备
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否禁用成功
     */
    boolean disableDevice(Long userId, String deviceId);

    /**
     * 启用设备
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否启用成功
     */
    boolean enableDevice(Long userId, String deviceId);

    /**
     * 删除设备
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否删除成功
     */
    boolean deleteDevice(Long userId, String deviceId);
}