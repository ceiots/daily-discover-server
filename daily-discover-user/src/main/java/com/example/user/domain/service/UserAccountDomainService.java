package com.example.user.domain.service;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.user.UserAccount;
import com.example.user.domain.model.user.UserAccountLog;
import com.example.user.domain.model.UserPointsLog;
import com.example.user.domain.model.id.UserId;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 用户账户领域服务
 * 专注于账户余额、积分等核心业务规则和不变性约束
 */
public interface UserAccountDomainService extends BaseDomainService {

    /**
     * 获取用户账户
     *
     * @param userId 用户ID
     * @return 用户账户
     */
    Optional<UserAccount> getUserAccount(UserId userId);

    /**
     * 创建用户账户
     *
     * @param userId 用户ID
     * @return 用户账户
     */
    UserAccount createUserAccount(UserId userId);

    /**
     * 增加余额
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param source 来源
     * @param sourceId 来源ID
     * @param remark 备注
     * @return 账户流水
     */
    UserAccountLog increaseBalance(UserId userId, BigDecimal amount, Integer source, String sourceId, String remark);

    /**
     * 减少余额
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param source 来源
     * @param sourceId 来源ID
     * @param remark 备注
     * @return 账户流水
     */
    UserAccountLog decreaseBalance(UserId userId, BigDecimal amount, Integer source, String sourceId, String remark);

    /**
     * 冻结金额
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param source 来源
     * @param sourceId 来源ID
     * @param remark 备注
     * @return 账户流水
     */
    UserAccountLog freezeAmount(UserId userId, BigDecimal amount, Integer source, String sourceId, String remark);

    /**
     * 解冻金额
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param source 来源
     * @param sourceId 来源ID
     * @param remark 备注
     * @return 账户流水
     */
    UserAccountLog unfreezeAmount(UserId userId, BigDecimal amount, Integer source, String sourceId, String remark);

    /**
     * 从冻结金额中扣减
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param source 来源
     * @param sourceId 来源ID
     * @param remark 备注
     * @return 账户流水
     */
    UserAccountLog decreaseFromFrozen(UserId userId, BigDecimal amount, Integer source, String sourceId, String remark);

    /**
     * 增加积分
     *
     * @param userId 用户ID
     * @param points 积分
     * @param source 来源
     * @param sourceId 来源ID
     * @param remark 备注
     * @return 账户流水
     */
    UserAccountLog increasePoints(UserId userId, Integer points, Integer source, String sourceId, String remark);

    /**
     * 减少积分
     *
     * @param userId 用户ID
     * @param points 积分
     * @param source 来源
     * @param sourceId 来源ID
     * @param remark 备注
     * @return 账户流水
     */
    UserAccountLog decreasePoints(UserId userId, Integer points, Integer source, String sourceId, String remark);

    /**
     * 增加成长值
     *
     * @param userId 用户ID
     * @param growthValue 成长值
     * @param source 来源
     * @param sourceId 来源ID
     * @param remark 备注
     * @return 账户流水
     */
    UserAccountLog increaseGrowthValue(UserId userId, Integer growthValue, Integer source, String sourceId, String remark);

    /**
     * 冻结账户
     *
     * @param userId 用户ID
     * @param remark 备注
     */
    void freezeAccount(UserId userId, String remark);

    /**
     * 解冻账户
     *
     * @param userId 用户ID
     * @param remark 备注
     */
    void unfreezeAccount(UserId userId, String remark);

    /**
     * 检查余额是否足够
     *
     * @param userId 用户ID
     * @param amount 金额
     * @return 是否足够
     */
    boolean isBalanceEnough(UserId userId, BigDecimal amount);

    /**
     * 检查积分是否足够
     *
     * @param userId 用户ID
     * @param points 积分
     * @return 是否足够
     */
    boolean isPointsEnough(UserId userId, Integer points);

    /**
     * 获取用户账户流水列表
     *
     * @param userId 用户ID
     * @param type   类型
     * @param limit  限制条数
     * @return 用户账户流水列表
     */
    List<UserAccountLog> getAccountLogs(Long userId, Integer type, Integer limit);

    /**
     * 获取用户积分记录列表
     *
     * @param userId 用户ID
     * @param type   类型
     * @param limit  限制条数
     * @return 用户积分记录列表
     */
    List<UserPointsLog> getPointsLogs(Long userId, Integer type, Integer limit);

    /**
     * 获取用户账户日志分页
     *
     * @param userId 用户ID
     * @param pageRequest 分页请求
     * @return 账户日志分页
     */
    PageResult<UserAccountLog> getAccountLogsByUserId(UserId userId, PageRequest pageRequest);

    /**
     * 获取用户账户日志列表（按类型和来源）
     *
     * @param userId 用户ID
     * @param type 类型
     * @param source 来源
     * @param limit 限制数量
     * @return 账户日志列表
     */
    List<UserAccountLog> getAccountLogsByUserIdAndTypeAndSource(UserId userId, Integer type, Integer source, Integer limit);
} 