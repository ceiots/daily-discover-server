package com.example.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderRequest {
    private Long userId;
    private List<Long> productIds; // 商品ID列表
    private String shippingAddress; // 收货地址
    // 可以根据需要添加更多订单相关的字段，例如支付方式、备注等
} 