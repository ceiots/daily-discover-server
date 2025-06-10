package com.example.user.domain.model;

import com.example.user.domain.model.id.UserId;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户账户流水实体
 */
@Getter
@Setter
public class UserAccountLog {

    /**
     * ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private UserId userId;

    /**
     * 类型:1-收入,2-支出,3-冻结,4-解冻,5-积分,6-成长值
     */
    private Integer type;

    /**
     * 变动金额
     */
    private BigDecimal amount;

    /**
     * 变动积分
     */
    private Integer points;

    /**
     * 变动成长值
     */
    private Integer growth;

    /**
     * 变动前金额
     */
    private BigDecimal beforeAmount;

    /**
     * 变动后金额
     */
    private BigDecimal afterAmount;

    /**
     * 来源:1-订单,2-退款,3-充值,4-提现,5-系统调整,6-签到,7-活动
     */
    private Integer source;

    /**
     * 来源ID
     */
    private String sourceId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建收入流水
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param beforeAmount 变动前金额
     * @param afterAmount 变动后金额
     * @param source 来源
     * @param sourceId 来源ID
     * @param remark 备注
     * @return 账户流水
     */
    public static UserAccountLog createIncomeLog(UserId userId, BigDecimal amount, BigDecimal beforeAmount, BigDecimal afterAmount,
                                              Integer source, String sourceId, String remark) {
        UserAccountLog log = new UserAccountLog();
        log.setUserId(userId);
        log.setType(1); // 收入
        log.setAmount(amount);
        log.setBeforeAmount(beforeAmount);
        log.setAfterAmount(afterAmount);
        log.setSource(source);
        log.setSourceId(sourceId);
        log.setRemark(remark);
        log.setCreateTime(LocalDateTime.now());
        return log;
    }

    /**
     * 创建支出流水
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param beforeAmount 变动前金额
     * @param afterAmount 变动后金额
     * @param source 来源
     * @param sourceId 来源ID
     * @param remark 备注
     * @return 账户流水
     */
    public static UserAccountLog createExpenseLog(UserId userId, BigDecimal amount, BigDecimal beforeAmount, BigDecimal afterAmount,
                                               Integer source, String sourceId, String remark) {
        UserAccountLog log = new UserAccountLog();
        log.setUserId(userId);
        log.setType(2); // 支出
        log.setAmount(amount);
        log.setBeforeAmount(beforeAmount);
        log.setAfterAmount(afterAmount);
        log.setSource(source);
        log.setSourceId(sourceId);
        log.setRemark(remark);
        log.setCreateTime(LocalDateTime.now());
        return log;
    }

    /**
     * 创建冻结流水
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param beforeAmount 变动前金额
     * @param afterAmount 变动后金额
     * @param source 来源
     * @param sourceId 来源ID
     * @param remark 备注
     * @return 账户流水
     */
    public static UserAccountLog createFreezeLog(UserId userId, BigDecimal amount, BigDecimal beforeAmount, BigDecimal afterAmount,
                                              Integer source, String sourceId, String remark) {
        UserAccountLog log = new UserAccountLog();
        log.setUserId(userId);
        log.setType(3); // 冻结
        log.setAmount(amount);
        log.setBeforeAmount(beforeAmount);
        log.setAfterAmount(afterAmount);
        log.setSource(source);
        log.setSourceId(sourceId);
        log.setRemark(remark);
        log.setCreateTime(LocalDateTime.now());
        return log;
    }

    /**
     * 创建解冻流水
     *
     * @param userId 用户ID
     * @param amount 金额
     * @param beforeAmount 变动前金额
     * @param afterAmount 变动后金额
     * @param source 来源
     * @param sourceId 来源ID
     * @param remark 备注
     * @return 账户流水
     */
    public static UserAccountLog createUnfreezeLog(UserId userId, BigDecimal amount, BigDecimal beforeAmount, BigDecimal afterAmount,
                                                Integer source, String sourceId, String remark) {
        UserAccountLog log = new UserAccountLog();
        log.setUserId(userId);
        log.setType(4); // 解冻
        log.setAmount(amount);
        log.setBeforeAmount(beforeAmount);
        log.setAfterAmount(afterAmount);
        log.setSource(source);
        log.setSourceId(sourceId);
        log.setRemark(remark);
        log.setCreateTime(LocalDateTime.now());
        return log;
    }

    /**
     * 创建积分流水
     *
     * @param userId 用户ID
     * @param points 积分
     * @param source 来源
     * @param sourceId 来源ID
     * @param remark 备注
     * @return 账户流水
     */
    public static UserAccountLog createPointsLog(UserId userId, Integer points, Integer source, String sourceId, String remark) {
        UserAccountLog log = new UserAccountLog();
        log.setUserId(userId);
        log.setType(5); // 积分
        log.setPoints(points);
        log.setSource(source);
        log.setSourceId(sourceId);
        log.setRemark(remark);
        log.setCreateTime(LocalDateTime.now());
        return log;
    }

    /**
     * 创建成长值流水
     *
     * @param userId 用户ID
     * @param growth 成长值
     * @param source 来源
     * @param sourceId 来源ID
     * @param remark 备注
     * @return 账户流水
     */
    public static UserAccountLog createGrowthLog(UserId userId, Integer growth, Integer source, String sourceId, String remark) {
        UserAccountLog log = new UserAccountLog();
        log.setUserId(userId);
        log.setType(6); // 成长值
        log.setGrowth(growth);
        log.setSource(source);
        log.setSourceId(sourceId);
        log.setRemark(remark);
        log.setCreateTime(LocalDateTime.now());
        return log;
    }
} 