package com.dailydiscover.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户积分交易记录实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_points_transactions")
public class UserPointsTransaction {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 交易类型：earn, use, expire, adjust
     */
    @TableField("transaction_type")
    private String transactionType;

    /**
     * 积分变化
     */
    @TableField("points_change")
    private Integer pointsChange;

    /**
     * 积分余额
     */
    @TableField("points_balance")
    private Integer pointsBalance;

    /**
     * 关联类型
     */
    @TableField("reference_type")
    private String referenceType;

    /**
     * 关联ID
     */
    @TableField("reference_id")
    private Long referenceId;

    /**
     * 交易描述
     */
    @TableField("description")
    private String description;

    /**
     * 交易时间
     */
    @TableField("transaction_time")
    private LocalDateTime transactionTime;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}