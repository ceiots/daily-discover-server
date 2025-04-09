package com.example.model;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
@Data
public class PaymentRequest {
    private Long userId;
    private List<Long> itemIds;
    private String paymentMethod;
    private String paymentPassword;
    private String paymentAmount;
    private String status;
    
    // 新增属性
    private String payType;
    private String orderNo;
    private BigDecimal totalAmount;
    private String subject;
    private String clientIp;


}