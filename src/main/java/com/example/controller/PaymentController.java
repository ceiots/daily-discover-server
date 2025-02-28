package com.example.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.api.CommonResult;
import com.example.model.PaymentRequest;
import com.example.model.PaymentResult;
import com.example.service.PaymentService;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/confirm")
    public CommonResult<PaymentResult> confirmPayment(@RequestBody PaymentRequest paymentRequest, HttpServletRequest request) {

        // 获取客户端IP地址
        String clientIp = getClientIp(request);
        paymentRequest.setClientIp(clientIp);

        // 调用支付服务处理支付逻辑
        PaymentResult paymentResult = paymentService.processPayment(paymentRequest);
        if (paymentResult.isSuccess()) {
            return CommonResult.success(paymentResult, "支付成功");
        } else {
            return CommonResult.failed(paymentResult.getMessage());
        }
    }

     private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}