package com.example.controller;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.api.CommonResult;
import com.example.model.PaymentRequest;
import com.example.model.PaymentResult;
import com.example.service.PaymentService;
import com.example.service.OrderService;
import com.example.model.Order;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderService orderService;

    /**
     * 处理支付请求
     */
    @PostMapping("/process")
    public CommonResult<PaymentResult> process(@RequestBody PaymentRequest paymentRequest, HttpServletRequest request) {
        System.out.println("处理支付请求: " + paymentRequest);
        // 获取客户端IP地址
        String clientIp = getClientIp(request);
        paymentRequest.setClientIp(clientIp);

        // 调用支付服务处理支付逻辑
        PaymentResult paymentResult = paymentService.processPayment(paymentRequest);
        if (paymentResult.isSuccess()) {
            // 支付成功，更新订单信息和状态
            Date paymentTime = new Date(); // 使用当前时间作为支付时间
            Order order = new Order();
            order.setOrderNumber(paymentRequest.getOrderNo());
            order.setStatus(OrderService.ORDER_STATUS_PENDING_DELIVERY); // 使用常量表示已支付/待发货状态
            order.setPaymentTime(paymentTime);
            
            // 设置支付方式，根据前端传入的支付方式转换
            if ("1".equals(paymentRequest.getPaymentMethod())) {
                order.setPaymentMethod(OrderService.PAYMENT_METHOD_ALIPAY);
            } else if ("2".equals(paymentRequest.getPaymentMethod())) {
                order.setPaymentMethod(OrderService.PAYMENT_METHOD_WECHAT);
            } else if ("3".equals(paymentRequest.getPaymentMethod())) {
                order.setPaymentMethod(OrderService.PAYMENT_METHOD_CREDIT_CARD);
            }
            
            // 更新订单状态和支付信息
            orderService.updateOrder(order);
            
            // 在返回结果中添加支付时间
            paymentResult.setPaymentTime(paymentTime);
            
            return CommonResult.success(paymentResult, "支付成功");
        } else {
            return CommonResult.failed(paymentResult.getMessage());
        }
    }

    /**
     * 更新订单状态或取消支付
     */
    @PostMapping("/set-status")
    public CommonResult<String> setOrderStatus(@RequestBody PaymentRequest paymentRequest) {
        try {
            String orderNumber = paymentRequest.getOrderNo();
            System.out.println("订单号：" + orderNumber + "，状态：" + paymentRequest.getStatus());
            int status;
            // 判断是否为取消支付
            if ("cancel".equalsIgnoreCase(paymentRequest.getStatus())) {
                status = OrderService.ORDER_STATUS_PENDING_PAYMENT;
            } else {
                status = Integer.parseInt(paymentRequest.getStatus());
            }
    
            boolean success = orderService.updateOrderStatus(orderNumber, status);
            if (success) {
                return CommonResult.success(null, "订单状态更新成功");
            } else {
                return CommonResult.failed("订单状态更新失败");
            }
        } catch (Exception e) {
            return CommonResult.failed("订单状态更新失败: " + e.getMessage());
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