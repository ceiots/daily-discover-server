package com.example.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentInfo {
    private String paymentMethod;
    private BigDecimal paymentAmount;
    // 支付宝/微信的交易号
    private String transactionId;
}