package com.example.user.application.service;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.application.dto.UserAccountDTO;
import com.example.user.application.dto.UserAccountLogDTO;
import com.example.user.application.dto.UserPointsLogDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户账户应用服务接口
 * 负责业务流程编排，调用领域服务实现业务逻辑
 */
public interface UserAccountService extends BaseApplicationService {

    /**
     * 获取用户账户
     *
     * @param userId 用户ID
     * @return 用户账户DTO
     */
    UserAccountDTO getUserAccount(Long userId);

    /**
     * 创建用户账户
     *
     * @param userId 用户ID
     * @return 用户账户DTO
     */
    UserAccountDTO createUserAccount(Long userId);

    /**
     * 增加余额
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param source 来源
     * @param sourceId 来源ID
     * @param remark 备注
     * @return 用户账户DTO
     */
    UserAccountDTO increaseBalance(Long userId, BigDecimal amount, Integer source, String sourceId, String remark);

    /**
     * 减少余额
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param source 来源
     * @param sourceId 来源ID
     * @param remark 备注
     * @return 用户账户DTO
     */
    UserAccountDTO decreaseBalance(Long userId, BigDecimal amount, Integer source, String sourceId, String remark);

    /**
     * 冻结金额
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param source 来源
     * @param sourceId 来源ID
     * @param remark 备注
     * @return 用户账户DTO
     */
    UserAccountDTO freezeAmount(Long userId, BigDecimal amount, Integer source, String sourceId, String remark);

    /**
     * 解冻金额
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param source 来源
     * @param sourceId 来源ID
     * @param remark 备注
     * @return 用户账户DTO
     */
    UserAccountDTO unfreezeAmount(Long userId, BigDecimal amount, Integer source, String sourceId, String remark);

    /**
     * 从冻结金额中扣减
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param source 来源
     * @param sourceId 来源ID
     * @param remark 备注
     * @return 用户账户DTO
     */
    UserAccountDTO decreaseFromFrozen(Long userId, BigDecimal amount, Integer source, String sourceId, String remark);

    /**
     * 增加积分
     *
     * @param userId 用户ID
     * @param points 积分
     * @param source 来源
     * @param sourceId 来源ID
     * @param remark 备注
     * @return 用户账户DTO
     */
    UserAccountDTO increasePoints(Long userId, Integer points, Integer source, String sourceId, String remark);

    /**
     * 减少积分
     *
     * @param userId 用户ID
     * @param points 积分
     * @param source 来源
     * @param sourceId 来源ID
     * @param remark 备注
     * @return 用户账户DTO
     */
    UserAccountDTO decreasePoints(Long userId, Integer points, Integer source, String sourceId, String remark);

    /**
     * 增加成长值
     *
     * @param userId 用户ID
     * @param growthValue 成长值
     * @param source 来源
     * @param sourceId 来源ID
     * @param remark 备注
     * @return 用户账户DTO
     */
    UserAccountDTO increaseGrowthValue(Long userId, Integer growthValue, Integer source, String sourceId, String remark);

    /**
     * 冻结账户
     *
     * @param userId 用户ID
     * @param remark 备注
     * @return 用户账户DTO
     */
    UserAccountDTO freezeAccount(Long userId, String remark);

    /**
     * 解冻账户
     *
     * @param userId 用户ID
     * @param remark 备注
     * @return 用户账户DTO
     */
    UserAccountDTO unfreezeAccount(Long userId, String remark);

    /**
     * 检查余额是否足够
     *
     * @param userId 用户ID
     * @param amount 金额
     * @return 是否足够
     */
    boolean isBalanceEnough(Long userId, BigDecimal amount);

    /**
     * 检查积分是否足够
     *
     * @param userId 用户ID
     * @param points 积分
     * @return 是否足够
     */
    boolean isPointsEnough(Long userId, Integer points);

    /**
     * 获取账户流水分页
     *
     * @param userId 用户ID
     * @param pageRequest 分页请求
     * @return 账户流水分页结果
     */
    PageResult<UserAccountLogDTO> getAccountLogs(Long userId, PageRequest pageRequest);

    /**
     * 获取账户流水列表
     *
     * @param userId 用户ID
     * @param type 类型
     * @param source 来源
     * @param limit 限制数量
     * @return 账户流水列表
     */
    List<UserAccountLogDTO> getAccountLogs(Long userId, Integer type, Integer source, Integer limit);

    /**
     * 获取用户积分记录列表
     *
     * @param userId 用户ID
     * @param type   类型
     * @param limit  限制条数
     * @return 用户积分记录DTO列表
     */
    List<UserPointsLogDTO> getPointsLogs(Long userId, Integer type, Integer limit);
} 