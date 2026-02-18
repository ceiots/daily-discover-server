package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券表实体类
 */
@Data
@TableName("coupons")
public class Coupon {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("coupon_code")
    private String couponCode;
    
    @TableField("coupon_name")
    private String couponName;
    
    @TableField("coupon_type")
    private String couponType;
    
    @TableField("discount_value")
    private BigDecimal discountValue;
    
    @TableField("min_order_amount")
    private BigDecimal minOrderAmount;
    
    @TableField("max_discount_amount")
    private BigDecimal maxDiscountAmount;
    
    @TableField("usage_limit")
    private Integer usageLimit;
    
    @TableField("total_quantity")
    private Integer totalQuantity;
    
    @TableField("used_quantity")
    private Integer usedQuantity;
    
    @TableField("valid_from")
    private LocalDateTime validFrom;
    
    @TableField("valid_to")
    private LocalDateTime validTo;
    
    @TableField("applicable_scope")
    private String applicableScope;
    
    @TableField("applicable_ids")
    private String applicableIds;
    
    @TableField("status")
    private String status;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}