package com.dailydiscover.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户优惠券实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_coupons")
public class UserCoupon {

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
     * 优惠券模板ID
     */
    @TableField("coupon_template_id")
    private Long couponTemplateId;

    /**
     * 优惠券名称
     */
    @TableField("coupon_name")
    private String couponName;

    /**
     * 优惠券类型：discount, amount
     */
    @TableField("coupon_type")
    private String couponType;

    /**
     * 优惠券面值
     */
    @TableField("coupon_value")
    private BigDecimal couponValue;

    /**
     * 最低消费金额
     */
    @TableField("min_amount")
    private BigDecimal minAmount;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 优惠券状态：unused, used, expired
     */
    @TableField("status")
    private String status;

    /**
     * 使用订单ID（如果已使用）
     */
    @TableField("used_order_id")
    private Long usedOrderId;

    /**
     * 使用时间
     */
    @TableField("used_time")
    private LocalDateTime usedTime;

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
     * 优惠券类型枚举
     */
    public enum CouponType {
        DISCOUNT("discount"),
        AMOUNT("amount");

        private final String value;

        CouponType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 优惠券状态枚举
     */
    public enum Status {
        UNUSED("unused"),
        USED("used"),
        EXPIRED("expired");

        private final String value;

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}