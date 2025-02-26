package com.example.model;

import lombok.Data;

@Data
public class PaymentResult {
    private boolean success;
    private String message;
    private String payForm;    // 支付宝表单
    private String codeUrl;    // 微信支付二维码链接
} 