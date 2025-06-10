package com.example.user.application.service;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.application.dto.UserLoginLogDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户登录日志应用服务接口
 */
public interface UserLoginLogService extends BaseApplicationService {

    /**
     * 记录登录日志
     *
     * @param userLoginLogDTO 用户登录日志DTO
     * @return 用户登录日志DTO
     */
    UserLoginLogDTO recordLoginLog(UserLoginLogDTO userLoginLogDTO);

    /**
     * 获取用户登录日志
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 用户登录日志DTO列表
     */
    List<UserLoginLogDTO> getUserLoginLogs(Long userId, Integer limit);

    /**
     * 分页获取用户登录日志
     *
     * @param userId 用户ID
     * @param pageRequest 分页请求
     * @return 用户登录日志分页结果
     */
    PageResult<UserLoginLogDTO> getUserLoginLogPage(Long userId, PageRequest pageRequest);

    /**
     * 获取用户最后一次登录日志
     *
     * @param userId 用户ID
     * @return 用户登录日志DTO
     */
    UserLoginLogDTO getLastLoginLog(Long userId);

    /**
     * 统计用户登录次数
     *
     * @param userId 用户ID
     * @return 登录次数
     */
    Long countLoginLogs(Long userId);

    /**
     * 统计用户登录失败次数
     *
     * @param userId 用户ID
     * @param hours 小时数
     * @return 登录失败次数
     */
    Integer countFailureLoginLogs(Long userId, Integer hours);

    /**
     * 清除用户登录失败记录
     *
     * @param userId 用户ID
     * @return 是否清除成功
     */
    boolean clearFailureLoginLogs(Long userId);

    /**
     * 获取用户指定时间段内的登录日志
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 用户登录日志DTO列表
     */
    List<UserLoginLogDTO> getUserLoginLogsByTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取用户指定设备的登录日志
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param limit 限制数量
     * @return 用户登录日志DTO列表
     */
    List<UserLoginLogDTO> getUserLoginLogsByDevice(Long userId, String deviceId, Integer limit);
}