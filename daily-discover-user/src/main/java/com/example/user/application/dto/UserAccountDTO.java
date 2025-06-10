package com.example.user.application.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户账户DTO
 */
@Data
@Accessors(chain = true)
public class UserAccountDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 账户余额
     */
    private BigDecimal balance;

    /**
     * 冻结金额
     */
    private BigDecimal freezeAmount;

    /**
     * 可用余额
     */
    private String availableBalance;

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
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 