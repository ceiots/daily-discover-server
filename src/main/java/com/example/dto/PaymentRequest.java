package com.example.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long orderId;
    private String paymentMethod; // 支付方式，例如 "Alipay", "WeChatPay"
    // 可以根据需要添加更多支付相关的字段，例如支付金额等
} 