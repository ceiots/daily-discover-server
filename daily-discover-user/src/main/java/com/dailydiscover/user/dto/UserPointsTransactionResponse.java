package com.dailydiscover.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户积分交易记录响应DTO
 */
@Data
public class UserPointsTransactionResponse {

    /**
     * 交易记录ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 交易类型：earn, use, expire, adjust
     */
    private String transactionType;

    /**
     * 积分变化
     */
    private Integer pointsChange;

    /**
     * 积分余额
     */
    private Integer pointsBalance;

    /**
     * 关联类型
     */
    private String referenceType;

    /**
     * 关联ID
     */
    private Long referenceId;

    /**
     * 交易描述
     */
    private String description;

    /**
     * 交易时间
     */
    private LocalDateTime transactionTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}