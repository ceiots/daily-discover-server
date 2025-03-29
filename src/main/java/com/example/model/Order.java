package com.example.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class Order {
    private Long id;
    private Long userId;           // 对应数据库 user_id
    private String orderNumber;    // 对应数据库 order_number
    private Date createdAt;        // 对应数据库 created_at
    private String paymentMethod;  // 对应数据库 payment_method
    private BigDecimal paymentAmount; // 对应数据库 payment_amount
    private Date paymentTime;      // 对应数据库 payment_time
    private Long orderAddrId;      // 对应数据库 order_addr_id
    private Integer status;        // 对应数据库 status
    private String shippingAddress; // 对应数据库 shipping_address
    private String statusStr;      // 添加状态描述字段
    private List<OrderItem> items;
    
    // 计算订单总金额的方法
    public BigDecimal calculateTotalAmount() {
        BigDecimal total = BigDecimal.ZERO;
        if (items != null) {
            for (OrderItem item : items) {
                total = total.add(item.getSubtotal());
            }
        }
        return total;
    }
}