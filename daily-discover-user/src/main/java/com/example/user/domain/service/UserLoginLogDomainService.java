package com.example.user.domain.service;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.UserLoginLog;
import com.example.user.domain.model.id.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户登录日志领域服务接口
 */
public interface UserLoginLogDomainService extends BaseDomainService {

    /**
     * 记录登录日志
     *
     * @param userLoginLog 用户登录日志
     * @return 保存后的用户登录日志
     */
    UserLoginLog recordLoginLog(UserLoginLog userLoginLog);

    /**
     * 获取用户登录日志
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 用户登录日志列表
     */
    List<UserLoginLog> getUserLoginLogs(UserId userId, Integer limit);

    /**
     * 分页获取用户登录日志
     *
     * @param userId 用户ID
     * @param pageRequest 分页请求
     * @return 用户登录日志分页结果
     */
    PageResult<UserLoginLog> getUserLoginLogPage(UserId userId, PageRequest pageRequest);

    /**
     * 获取用户最后一次登录日志
     *
     * @param userId 用户ID
     * @return 用户登录日志可选值
     */
    Optional<UserLoginLog> getLastLoginLog(UserId userId);

    /**
     * 统计用户登录次数
     *
     * @param userId 用户ID
     * @return 登录次数
     */
    Long countLoginLogs(UserId userId);

    /**
     * 统计用户登录失败次数
     *
     * @param userId 用户ID
     * @param hours 小时数
     * @return 登录失败次数
     */
    Integer countFailureLoginLogs(UserId userId, Integer hours);

    /**
     * 清除用户登录失败记录
     *
     * @param userId 用户ID
     * @return 是否清除成功
     */
    boolean clearFailureLoginLogs(UserId userId);

    /**
     * 获取用户指定时间段内的登录日志
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 用户登录日志列表
     */
    List<UserLoginLog> getUserLoginLogsByTimeRange(UserId userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取用户指定设备的登录日志
     *
     * @param userId 用户ID
     * @param deviceId 设备ID
     * @param limit 限制数量
     * @return 用户登录日志列表
     */
    List<UserLoginLog> getUserLoginLogsByDevice(UserId userId, String deviceId, Integer limit);
}