package com.example.model;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class Order {
    private Long id;
    private Long userId;
    private List<Long> productIds; // 商品ID列表
    private String shippingAddress; // 收货地址
    private String status; // 订单状态，例如 "Pending", "Shipped", "Delivered"
    private Date createdAt; // 订单创建时间
    // 可以根据需要添加更多订单相关的字段，例如总金额、支付方式等
} 