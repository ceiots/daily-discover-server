package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单核心表实体类
 */
@Data
@TableName("orders_core")
public class OrdersCore {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("order_no")
    private String orderNo;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("total_amount")
    private BigDecimal totalAmount;
    
    @TableField("actual_amount")
    private BigDecimal actualAmount;
    
    @TableField("payment_status")
    private String paymentStatus;
    
    @TableField("status")
    private Integer status;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}