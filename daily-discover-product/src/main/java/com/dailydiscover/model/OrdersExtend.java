package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单扩展表实体类
 */
@Data
@TableName("orders_extend")
public class OrdersExtend {
    
    @TableId(value = "order_id", type = IdType.INPUT)
    private Long orderId;
    
    @TableField("address_id")
    private Long addressId;
    
    @TableField("province_id")
    private String provinceId;
    
    @TableField("city_id")
    private String cityId;
    
    @TableField("district_id")
    private String districtId;
    
    @TableField("detailed_address")
    private String detailedAddress;
    
    @TableField("coupon_id")
    private Long couponId;
    
    @TableField("coupon_discount_amount")
    private BigDecimal couponDiscountAmount;
    
    @TableField("discount_amount")
    private BigDecimal discountAmount;
    
    @TableField("payment_method_id")
    private Long paymentMethodId;
    
    @TableField("shipping_id")
    private Long shippingId;
    
    @TableField("invoice_id")
    private Long invoiceId;
    
    @TableField("paid_at")
    private LocalDateTime paidAt;
    
    @TableField("completed_at")
    private LocalDateTime completedAt;
}