package com.example.user.domain.model;

import com.example.common.exception.BusinessException;
import com.example.common.result.ResultCode;
import com.example.user.domain.model.id.UserId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 用户账户领域模型
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAccount {

    /**
     * ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private UserId userId;

    /**
     * 账户余额
     */
    private BigDecimal balance;

    /**
     * 冻结金额
     */
    private BigDecimal freezeAmount;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 成长值
     */
    private Integer growthValue;

    /**
     * 状态:0-冻结,1-正常
     */
    private Integer status;

    /**
     * 版本号，用于乐观锁
     */
    private Integer version;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建账户
     */
    public static UserAccount create(UserId userId) {
        UserAccount account = new UserAccount();
        account.userId = userId;
        account.balance = BigDecimal.ZERO;
        account.freezeAmount = BigDecimal.ZERO;
        account.points = 0;
        account.growthValue = 0;
        account.status = 1;
        account.version = 0;
        account.createTime = LocalDateTime.now();
        account.updateTime = LocalDateTime.now();
        return account;
    }

    /**
     * 增加余额
     */
    public void addBalance(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "金额必须大于0");
        }
        if (status != 1) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED, "账户已被冻结");
        }
        this.balance = this.balance.add(amount);
        this.updateTime = LocalDateTime.now();
        this.version++;
    }

    /**
     * 减少余额
     */
    public void subtractBalance(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "金额必须大于0");
        }
        if (status != 1) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED, "账户已被冻结");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new BusinessException(ResultCode.BALANCE_NOT_ENOUGH, "余额不足");
        }
        this.balance = this.balance.subtract(amount);
        this.updateTime = LocalDateTime.now();
        this.version++;
    }

    /**
     * 冻结金额
     */
    public void freezeAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "金额必须大于0");
        }
        if (status != 1) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED, "账户已被冻结");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new BusinessException(ResultCode.BALANCE_NOT_ENOUGH, "余额不足");
        }
        this.balance = this.balance.subtract(amount);
        this.freezeAmount = this.freezeAmount.add(amount);
        this.updateTime = LocalDateTime.now();
        this.version++;
    }

    /**
     * 解冻金额
     */
    public void unfreezeAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "金额必须大于0");
        }
        if (this.freezeAmount.compareTo(amount) < 0) {
            throw new BusinessException(ResultCode.FREEZE_AMOUNT_NOT_ENOUGH, "冻结金额不足");
        }
        this.freezeAmount = this.freezeAmount.subtract(amount);
        this.balance = this.balance.add(amount);
        this.updateTime = LocalDateTime.now();
        this.version++;
    }

    /**
     * 解冻并扣除金额
     */
    public void unfreezeAndSubtract(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "金额必须大于0");
        }
        if (this.freezeAmount.compareTo(amount) < 0) {
            throw new BusinessException(ResultCode.FREEZE_AMOUNT_NOT_ENOUGH, "冻结金额不足");
        }
        this.freezeAmount = this.freezeAmount.subtract(amount);
        this.updateTime = LocalDateTime.now();
        this.version++;
    }

    /**
     * 增加积分
     */
    public void addPoints(Integer points) {
        if (points <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "积分必须大于0");
        }
        if (status != 1) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED, "账户已被冻结");
        }
        this.points += points;
        this.updateTime = LocalDateTime.now();
        this.version++;
    }

    /**
     * 减少积分
     */
    public void subtractPoints(Integer points) {
        if (points <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "积分必须大于0");
        }
        if (status != 1) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED, "账户已被冻结");
        }
        if (this.points < points) {
            throw new BusinessException(ResultCode.POINTS_NOT_ENOUGH, "积分不足");
        }
        this.points -= points;
        this.updateTime = LocalDateTime.now();
        this.version++;
    }

    /**
     * 增加成长值
     */
    public void addGrowthValue(Integer growthValue) {
        if (growthValue <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "成长值必须大于0");
        }
        if (status != 1) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED, "账户已被冻结");
        }
        this.growthValue += growthValue;
        this.updateTime = LocalDateTime.now();
        this.version++;
    }

    /**
     * 冻结账户
     */
    public void freeze() {
        this.status = 0;
        this.updateTime = LocalDateTime.now();
        this.version++;
    }

    /**
     * 解冻账户
     */
    public void unfreeze() {
        this.status = 1;
        this.updateTime = LocalDateTime.now();
        this.version++;
    }

    /**
     * 是否冻结
     */
    public boolean isFrozen() {
        return this.status == 0;
    }

    /**
     * 可用余额（余额 - 冻结金额）
     */
    public BigDecimal getAvailableBalance() {
        return this.balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAccount that = (UserAccount) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
} 