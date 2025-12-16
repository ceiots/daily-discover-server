package com.dailydiscover.common.payment.controller;

import com.dailydiscover.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付控制器
 */
@Slf4j
@RestController
@RequestMapping("/common/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    /**
     * 创建支付订单
     */
    @PostMapping("/create")
    public ResponseEntity<Result<Map<String, Object>>> createPayment(@RequestParam String orderId, 
                                                                     @RequestParam Double amount) {
        try {
            log.info("创建支付订单: orderId={}, amount={}", orderId, amount);
            
            Map<String, Object> payment = new HashMap<>();
            payment.put("paymentId", "PAY_" + System.currentTimeMillis());
            payment.put("orderId", orderId);
            payment.put("amount", amount);
            payment.put("status", "pending");
            payment.put("payUrl", "https://payment.dailydiscover.com/pay/" + orderId);
            
            log.info("支付订单创建成功: {}", payment.get("paymentId"));
            return ResponseEntity.ok(Result.success(payment));
        } catch (Exception e) {
            log.error("创建支付订单失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 查询支付状态
     */
    @GetMapping("/status/{paymentId}")
    public ResponseEntity<Result<Map<String, Object>>> getPaymentStatus(@PathVariable String paymentId) {
        try {
            log.debug("查询支付状态: {}", paymentId);
            
            Map<String, Object> status = new HashMap<>();
            status.put("paymentId", paymentId);
            status.put("status", "success");
            status.put("paidAt", System.currentTimeMillis());
            
            return ResponseEntity.ok(Result.success(status));
        } catch (Exception e) {
            log.error("查询支付状态失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 退款
     */
    @PostMapping("/refund")
    public ResponseEntity<Result<Map<String, Object>>> refund(@RequestParam String paymentId) {
        try {
            log.info("处理退款: {}", paymentId);
            
            Map<String, Object> refund = new HashMap<>();
            refund.put("refundId", "REF_" + System.currentTimeMillis());
            refund.put("paymentId", paymentId);
            refund.put("status", "processing");
            
            log.info("退款处理成功: {}", refund.get("refundId"));
            return ResponseEntity.ok(Result.success(refund));
        } catch (Exception e) {
            log.error("退款处理失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }
}