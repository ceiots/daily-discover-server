package com.example.user.domain.repository;

import com.example.user.domain.model.UserLoginLog;

import java.util.List;
import java.util.Optional;

/**
 * 用户登录日志仓储接口
 */
public interface UserLoginLogRepository {

    /**
     * 通过ID获取用户登录日志
     *
     * @param id 日志ID
     * @return 用户登录日志对象
     */
    Optional<UserLoginLog> findById(Long id);

    /**
     * 保存用户登录日志
     *
     * @param loginLog 用户登录日志对象
     * @return 保存后的用户登录日志对象
     */
    UserLoginLog save(UserLoginLog loginLog);

    /**
     * 查询用户登录日志列表
     *
     * @param userId 用户ID
     * @param limit  限制条数
     * @return 用户登录日志列表
     */
    List<UserLoginLog> findByUserId(Long userId, Integer limit);

    /**
     * 查询用户最后一次登录日志
     *
     * @param userId 用户ID
     * @return 用户登录日志对象
     */
    Optional<UserLoginLog> findLastLoginLog(Long userId);

    /**
     * 统计用户登录次数
     *
     * @param userId 用户ID
     * @return 登录次数
     */
    Integer countByUserId(Long userId);

    /**
     * 统计用户登录失败次数
     *
     * @param userId 用户ID
     * @param hours  小时数
     * @return 登录失败次数
     */
    Integer countFailureByUserId(Long userId, Integer hours);

    /**
     * 清除用户登录失败记录
     *
     * @param userId 用户ID
     * @return 是否清除成功
     */
    boolean clearFailureByUserId(Long userId);
} 