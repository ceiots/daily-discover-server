package com.example.model;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

@Data
public class Order {
    private Long id;
    private Long userId;
    private String orderNumber;
    private Date createTime;
    private List<OrderItem> items; // 订单商品项列表
    private String shippingAddress; // 收货地址
    private Integer status; // 订单状态，使用整数类型
    private Date createdAt; // 订单创建时间
    private String paymentMethod; // 支付方式
    private BigDecimal paymentAmount; // 支付金额
    private Date paymentTime; // 支付时间

    // 订单商品项内部类
    @Data
    public static class OrderItem {
        private Long productId; // 商品ID
        private Integer quantity; // 商品数量
        private BigDecimal price;      // 添加商品单价
        private BigDecimal subtotal;   // 添加小计金额
    }

    // 计算订单总金额的方法
    public Double calculateTotalAmount() {
        double total = 0;
        if (items != null) {
            for (OrderItem item : items) {
                // 这里假设每个商品的单价可以通过 productId 从数据库中获取
                // 实际应用中需要调用商品服务获取单价
                // 这里简单假设单价为 100
                total += 100 * item.getQuantity();
            }
        }
        return total;
    }

}