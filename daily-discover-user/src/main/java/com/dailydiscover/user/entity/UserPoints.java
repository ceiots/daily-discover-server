package com.dailydiscover.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户积分实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_points")
public class UserPoints {

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
     * 积分变动类型：earn, consume, expire
     */
    @TableField("change_type")
    private String changeType;

    /**
     * 积分变动数量（正数表示获得，负数表示消耗）
     */
    @TableField("change_amount")
    private Integer changeAmount;

    /**
     * 变动后积分余额
     */
    @TableField("balance_after")
    private Integer balanceAfter;

    /**
     * 积分来源/用途描述
     */
    @TableField("description")
    private String description;

    /**
     * 关联的订单ID（如果适用）
     */
    @TableField("related_order_id")
    private Long relatedOrderId;

    /**
     * 积分过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

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

    /**
     * 积分变动类型枚举
     */
    public enum ChangeType {
        EARN("earn"),
        CONSUME("consume"),
        EXPIRE("expire");

        private final String value;

        ChangeType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}