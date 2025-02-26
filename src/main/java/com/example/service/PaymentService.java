package com.example.service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.example.config.AlipayConfig;
import com.example.config.WxPayConfig;
import com.example.enums.PaymentTypeEnum;
import com.example.model.PaymentRequest;
import com.example.model.PaymentResult;
import com.github.wxpay.sdk.WXPay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class PaymentService {

    //@Autowired
    private AlipayConfig alipayConfig;

    //@Autowired
    private WxPayConfig wxPayConfig;

    public PaymentResult processPayment(PaymentRequest paymentRequest) {
        PaymentResult result = new PaymentResult();
        
        try {
            // 根据支付类型选择对应的支付方式
            if (PaymentTypeEnum.ALIPAY.getCode().equals(paymentRequest.getPayType())) {
                return processAlipay(paymentRequest);
            } else if (PaymentTypeEnum.WXPAY.getCode().equals(paymentRequest.getPayType())) {
                return processWxPay(paymentRequest);
            } else {
                result.setSuccess(false);
                result.setMessage("不支持的支付类型");
                return result;
            }
        } catch (Exception e) {
            log.error("支付处理异常", e);
            result.setSuccess(false);
            result.setMessage("支付处理失败：" + e.getMessage());
            return result;
        }
    }

    private PaymentResult processAlipay(PaymentRequest paymentRequest) throws AlipayApiException {
        PaymentResult result = new PaymentResult();
        
        // 初始化AlipayClient
        AlipayClient alipayClient = new DefaultAlipayClient(
            alipayConfig.getGatewayUrl(),
            alipayConfig.getAppId(),
            alipayConfig.getPrivateKey(),
            "json",
            "UTF-8",
            alipayConfig.getPublicKey(),
            "RSA2"
        );

        // 创建API对应的request
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(alipayConfig.getReturnUrl());
        alipayRequest.setNotifyUrl(alipayConfig.getNotifyUrl());

        // 构建请求参数
        Map<String, Object> bizContent = new HashMap<>();
        bizContent.put("out_trade_no", paymentRequest.getOrderNo());
        bizContent.put("total_amount", paymentRequest.getTotalAmount());
        bizContent.put("subject", paymentRequest.getSubject());
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        
        alipayRequest.setBizContent(com.alibaba.fastjson.JSON.toJSONString(bizContent));
        
        // 调用支付宝接口
        String form = alipayClient.pageExecute(alipayRequest).getBody();
        
        result.setSuccess(true);
        result.setPayForm(form);
        return result;
    }

    private PaymentResult processWxPay(PaymentRequest paymentRequest) throws Exception {
        PaymentResult result = new PaymentResult();
        
        // 初始化WXPay
        WXPay wxpay = new WXPay(wxPayConfig);

        Map<String, String> data = new HashMap<>();
        data.put("body", paymentRequest.getSubject());
        data.put("out_trade_no", paymentRequest.getOrderNo());
        data.put("total_fee", String.valueOf(paymentRequest.getTotalAmount().multiply(new java.math.BigDecimal("100")).intValue()));
        data.put("spbill_create_ip", paymentRequest.getClientIp());
        data.put("notify_url", wxPayConfig.getNotifyUrl());
        data.put("trade_type", "NATIVE");  // 根据实际场景选择支付方式

        // 调用微信支付接口
        Map<String, String> resp = wxpay.unifiedOrder(data);

        if ("SUCCESS".equals(resp.get("return_code")) && "SUCCESS".equals(resp.get("result_code"))) {
            result.setSuccess(true);
            result.setCodeUrl(resp.get("code_url")); // 二维码链接
        } else {
            result.setSuccess(false);
            result.setMessage(resp.get("return_msg"));
        }
        
        return result;
    }
} 