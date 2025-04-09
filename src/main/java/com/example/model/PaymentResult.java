package com.example.model;

import lombok.Data;
import java.util.Date;

@Data
public class PaymentResult {
    private boolean success;
    private String message;
    private String payForm;    // 支付宝表单
    private String codeUrl;    // 微信支付二维码链接
    private Date paymentTime;  // 支付时间

    public static PaymentResult fail(String message) {
        PaymentResult result = new PaymentResult();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }

    public static PaymentResult success(String message) {
        PaymentResult result = new PaymentResult();
        result.setSuccess(true);
        result.setMessage(message);
        result.setPaymentTime(new Date()); // 设置当前时间为支付时间
        return result;
    }
} 