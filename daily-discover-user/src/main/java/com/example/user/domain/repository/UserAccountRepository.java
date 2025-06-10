package com.example.user.domain.repository;

import com.example.user.domain.model.UserPointsLog;
import com.example.user.domain.model.UserAccount;
import com.example.user.domain.model.UserAccountLog;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 用户账户仓储接口
 */
public interface UserAccountRepository {

    /**
     * 通过ID获取用户账户
     *
     * @param id 账户ID
     * @return 用户账户对象
     */
    Optional<UserAccount> findById(Long id);

    /**
     * 通过用户ID获取用户账户
     *
     * @param userId 用户ID
     * @return 用户账户对象
     */
    Optional<UserAccount> findByUserId(Long userId);

    /**
     * 保存用户账户
     *
     * @param userAccount 用户账户对象
     * @return 保存后的用户账户对象
     */
    UserAccount save(UserAccount userAccount);

    /**
     * 更新用户账户
     *
     * @param userAccount 用户账户对象
     * @return 更新后的用户账户对象
     */
    UserAccount update(UserAccount userAccount);

    /**
     * 更新用户账户余额
     *
     * @param userId 用户ID
     * @param amount 变动金额
     * @return 是否更新成功
     */
    boolean updateBalance(Long userId, BigDecimal amount);

    /**
     * 冻结用户账户余额
     *
     * @param userId 用户ID
     * @param amount 冻结金额
     * @return 是否冻结成功
     */
    boolean freezeBalance(Long userId, BigDecimal amount);

    /**
     * 解冻用户账户余额
     *
     * @param userId 用户ID
     * @param amount 解冻金额
     * @return 是否解冻成功
     */
    boolean unfreezeBalance(Long userId, BigDecimal amount);

    /**
     * 更新用户积分
     *
     * @param userId 用户ID
     * @param points 变动积分
     * @return 是否更新成功
     */
    boolean updatePoints(Long userId, Integer points);

    /**
     * 更新用户成长值
     *
     * @param userId 用户ID
     * @param growth 变动成长值
     * @return 是否更新成功
     */
    boolean updateGrowth(Long userId, Integer growth);

    /**
     * 保存用户账户流水
     *
     * @param accountLog 用户账户流水对象
     * @return 保存后的用户账户流水对象
     */
    UserAccountLog saveAccountLog(UserAccountLog accountLog);

    /**
     * 查询用户账户流水列表
     *
     * @param userId 用户ID
     * @param type   类型
     * @param limit  限制条数
     * @return 用户账户流水列表
     */
    List<UserAccountLog> findAccountLogs(Long userId, Integer type, Integer limit);

    /**
     * 保存用户积分记录
     *
     * @param pointsLog 用户积分记录对象
     * @return 保存后的用户积分记录对象
     */
    UserPointsLog savePointsLog(UserPointsLog pointsLog);

    /**
     * 查询用户积分记录列表
     *
     * @param userId 用户ID
     * @param type   类型
     * @param limit  限制条数
     * @return 用户积分记录列表
     */
    List<UserPointsLog> findPointsLogs(Long userId, Integer type, Integer limit);
} 