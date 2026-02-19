package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券使用记录表实体类
 */
@Data
@TableName("coupon_usage_records")
public class CouponUsageRecord {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("user_coupon_id")
    private Long userCouponId;
    
    @TableField("order_id")
    private Long orderId;
    
    @TableField("coupon_id")
    private Long couponId;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("discount_amount")
    private BigDecimal discountAmount;
    
    @TableField("order_amount")
    private BigDecimal orderAmount;
    
    @TableField("used_at")
    private LocalDateTime usedAt;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
}