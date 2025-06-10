package com.example.user.domain.repository;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.device.UserDevice;
import com.example.user.domain.model.id.UserId;

import java.util.List;
import java.util.Optional;

/**
 * 用户设备仓储接口
 */
public interface UserDeviceRepository {

    /**
     * 根据ID查询用户设备
     *
     * @param id 设备ID
     * @return 用户设备
     */
    Optional<UserDevice> findById(Long id);

    /**
     * 根据用户ID和设备ID查询用户设备
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 用户设备
     */
    Optional<UserDevice> findByUserIdAndDeviceId(UserId userId, String deviceId);

    /**
     * 根据设备ID查询用户设备
     *
     * @param deviceId 设备ID
     * @return 用户设备
     */
    Optional<UserDevice> findByDeviceId(String deviceId);

    /**
     * 根据用户ID查询用户设备列表
     *
     * @param userId 用户ID
     * @return 用户设备列表
     */
    List<UserDevice> findByUserId(UserId userId);

    /**
     * 分页查询用户设备
     *
     * @param userId 用户ID
     * @param pageRequest 分页请求
     * @return 分页结果
     */
    PageResult<UserDevice> findPage(UserId userId, PageRequest pageRequest);

    /**
     * 保存用户设备
     *
     * @param userDevice 用户设备
     * @return 保存后的用户设备
     */
    UserDevice save(UserDevice userDevice);

    /**
     * 更新用户设备
     *
     * @param userDevice 用户设备
     * @return 更新后的用户设备
     */
    UserDevice update(UserDevice userDevice);

    /**
     * 删除用户设备
     *
     * @param id 设备ID
     * @return 是否删除成功
     */
    boolean delete(Long id);

    /**
     * 根据用户ID和设备ID删除用户设备
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否删除成功
     */
    boolean deleteByUserIdAndDeviceId(UserId userId, String deviceId);

    /**
     * 更新推送Token
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param pushToken 推送Token
     * @return 是否更新成功
     */
    boolean updatePushToken(UserId userId, String deviceId, String pushToken);

    /**
     * 更新设备状态
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param status 状态
     * @return 是否更新成功
     */
    boolean updateStatus(UserId userId, String deviceId, Integer status);

    /**
     * 统计用户设备数量
     *
     * @param userId 用户ID
     * @return 设备数量
     */
    int countByUserId(UserId userId);

    /**
     * 检查设备是否存在
     *
     * @param deviceId 设备ID
     * @return 是否存在
     */
    boolean existsByDeviceId(String deviceId);

    /**
     * 检查用户设备是否存在
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @return 是否存在
     */
    boolean existsByUserIdAndDeviceId(UserId userId, String deviceId);
}