package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单物流信息表
 */
@Data
@TableName("order_shipping")
public class OrderShipping {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("order_id")
    private Long orderId;
    
    @TableField("shipping_company")
    private String shippingCompany;
    
    @TableField("shipping_no")
    private String shippingNo;
    
    @TableField("shipping_fee")
    private BigDecimal shippingFee;
    
    @TableField("shipping_status")
    private Integer shippingStatus;
    
    @TableField("shipped_at")
    private LocalDateTime shippedAt;
    
    @TableField("estimated_delivery_at")
    private LocalDateTime estimatedDeliveryAt;
    
    @TableField("delivered_at")
    private LocalDateTime deliveredAt;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}