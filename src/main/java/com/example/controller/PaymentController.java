package com.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.dto.PaymentRequest;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @PostMapping("/pay")
    public ResponseEntity<?> payOrder(@RequestBody PaymentRequest paymentRequest) {
        // 调用支付服务进行支付
        return ResponseEntity.ok().body("支付成功");
    }
} 