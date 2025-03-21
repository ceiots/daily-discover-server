package com.example.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class Order {
    private Long id;
    private Long userId;
    private String orderNumber;
    private Date createdAt;
    private String paymentMethod;
    private BigDecimal paymentAmount;
    private Date paymentTime;
    private List<OrderItem> items;
    private Long orderAddrId; // 新增字段，关联收货信息
    // 修改status字段类型为int
    private int status; 
    // 新增shippingAddress属性
    private String shippingAddress; 

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