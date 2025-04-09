package com.example.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.example.config.AlipayConfig;
import com.example.config.WxPayConfig;
import com.example.model.PaymentRequest;
import com.example.model.PaymentResult;
import com.github.wxpay.sdk.WXPay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;

@Slf4j
@Service
public class PaymentService {

    //@Autowired
    private AlipayConfig alipayConfig;

    //@Autowired
    private WxPayConfig wxPayConfig;

    /**
     * 处理支付请求
     */
    public PaymentResult processPayment(PaymentRequest paymentRequest) {
        log.info("处理支付请求: {}", paymentRequest);
        PaymentResult result = new PaymentResult();

        try {
            // 验证支付密码
            if (paymentRequest.getPaymentPassword() != null) {
                if (!verifyPaymentPassword(paymentRequest.getPaymentPassword())) {
                    result.setSuccess(false);
                    result.setMessage("支付密码错误");
                    return result;
                }
            }

            // 判断支付方式
            if ("1".equals(paymentRequest.getPaymentMethod())) {
                // 支付宝支付
                result.setSuccess(true);
                result.setMessage("支付宝支付成功");
                result.setPaymentTime(new Date());
                // 实际项目中这里应该调用支付宝接口
                // return processAlipay(paymentRequest);
            } else if ("2".equals(paymentRequest.getPaymentMethod())) {
                // 微信支付
                result.setSuccess(true);
                result.setMessage("微信支付成功");
                result.setPaymentTime(new Date());
                // 实际项目中这里应该调用微信支付接口
                // return processWxPay(paymentRequest);
            } else if ("3".equals(paymentRequest.getPaymentMethod())) {
                // 密码支付
                result.setSuccess(true);
                result.setMessage("密码支付成功");
                result.setPaymentTime(new Date());
            } else {
                result.setSuccess(false);
                result.setMessage("不支持的支付方式");
            }

            return result;
        } catch (Exception e) {
            log.error("支付处理异常", e);
            result.setSuccess(false);
            result.setMessage("支付处理失败：" + e.getMessage());
            return result;
        }
    }

    /**
     * 验证支付密码
     */
    private boolean verifyPaymentPassword(String password) {
        // 模拟密码验证逻辑，实际项目中应该调用密码验证服务
        log.info("验证支付密码");
        return "123456".equals(password);
    }

    /**
     * 处理支付宝支付
     */
    public PaymentResult processAlipay(PaymentRequest paymentRequest) throws AlipayApiException {
        log.info("处理支付宝支付请求: {}", paymentRequest);
        PaymentResult result = new PaymentResult();

        // 初始化AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(
                alipayConfig.getGatewayUrl(),
                alipayConfig.getAppId(),
                alipayConfig.getPrivateKey(),
                "json",
                "UTF-8",
                alipayConfig.getPublicKey(),
                "RSA2");

        // 创建API对应的request
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(alipayConfig.getReturnUrl());
        alipayRequest.setNotifyUrl(alipayConfig.getNotifyUrl());

        // 构建请求参数
        Map<String, Object> bizContent = new HashMap<>();
        bizContent.put("out_trade_no", paymentRequest.getOrderNo());
        bizContent.put("total_amount", paymentRequest.getTotalAmount());
        bizContent.put("subject", "支付订单" + paymentRequest.getOrderNo());
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");

        alipayRequest.setBizContent(JSON.toJSONString(bizContent));

        // 调用支付宝接口
        String form = alipayClient.pageExecute(alipayRequest).getBody();

        result.setSuccess(true);
        result.setPayForm(form);
        result.setPaymentTime(new Date());
        return result;
    }

    /**
     * 处理微信支付
     */
    public PaymentResult processWxPay(PaymentRequest paymentRequest) throws Exception {
        log.info("处理微信支付请求: {}", paymentRequest);
        PaymentResult result = new PaymentResult();

        // 初始化WXPay
        WXPay wxpay = new WXPay(wxPayConfig);

        Map<String, String> data = new HashMap<>();
        data.put("body", "支付订单" + paymentRequest.getOrderNo());
        data.put("out_trade_no", paymentRequest.getOrderNo());
        data.put("total_fee",
                String.valueOf(paymentRequest.getTotalAmount().multiply(new java.math.BigDecimal("100")).intValue()));
        data.put("spbill_create_ip", paymentRequest.getClientIp());
        data.put("notify_url", wxPayConfig.getNotifyUrl());
        data.put("trade_type", "NATIVE"); // 根据实际场景选择支付方式

        // 调用微信支付接口
        Map<String, String> resp = wxpay.unifiedOrder(data);

        if ("SUCCESS".equals(resp.get("return_code")) && "SUCCESS".equals(resp.get("result_code"))) {
            result.setSuccess(true);
            result.setCodeUrl(resp.get("code_url")); // 二维码链接
            result.setPaymentTime(new Date());
        } else {
            result.setSuccess(false);
            result.setMessage(resp.get("return_msg"));
        }

        return result;
    }

}