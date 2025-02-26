package com.example.controller;

import com.example.service.PaymentService;
import com.example.model.PaymentRequest;
import com.example.model.PaymentResult;
import com.example.common.api.CommonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/confirm")
    public CommonResult<PaymentResult> confirmPayment(@RequestBody PaymentRequest paymentRequest) {
        // 调用支付服务处理支付逻辑
        PaymentResult paymentResult = paymentService.processPayment(paymentRequest);
        if (paymentResult.isSuccess()) {
            return CommonResult.success(paymentResult, "支付成功");
        } else {
            return CommonResult.failed(paymentResult.getMessage());
        }
    }
}      if (paymentResult.isSuccess()) {
            return CommonResult.success(paymentResult, "支付成功");
        } else {
            return CommonResult.failed(paymentResult.getMessage());
        }
    }
}