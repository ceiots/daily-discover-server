package com.example.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.common.ApiResponse;
import com.example.dto.PaymentPasswordRequest;
import com.example.service.UserPaymentService;

/**
 * 用户支付密码控制器
 */
@RestController
@RequestMapping("/user/payment-password")
public class UserPaymentController {

    @Autowired
    private UserPaymentService userPaymentService;

    /**
     * 查询用户支付密码状态
     */
    @GetMapping("/status")
    public ResponseEntity<ApiResponse> getPaymentPasswordStatus(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(new ApiResponse(401, "未登录", null));
        }
        
        Map<String, Object> result = userPaymentService.getPaymentPasswordStatus(userId);
        return ResponseEntity.ok(new ApiResponse(200, "获取成功", result));
    }

    /**
     * 发送支付密码验证码
     */
    @PostMapping("/send-code")
    public ResponseEntity<ApiResponse> sendVerificationCode(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(new ApiResponse(401, "未登录", null));
        }
        
        boolean success = userPaymentService.sendVerificationCode(userId);
        if (success) {
            return ResponseEntity.ok(new ApiResponse(200, "验证码发送成功", null));
        } else {
            return ResponseEntity.ok(new ApiResponse(500, "验证码发送失败", null));
        }
    }

    /**
     * 验证支付密码
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse> verifyPaymentPassword(@RequestBody PaymentPasswordRequest request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(new ApiResponse(401, "未登录", null));
        }
        
        boolean valid = userPaymentService.verifyPaymentPassword(userId, request.getPaymentPassword());
        if (valid) {
            return ResponseEntity.ok(new ApiResponse(200, "验证成功", null));
        } else {
            return ResponseEntity.ok(new ApiResponse(400, "支付密码错误", null));
        }
    }

    /**
     * 更新支付密码
     */
    @PostMapping("/update")
    public ResponseEntity<ApiResponse> updatePaymentPassword(@RequestBody PaymentPasswordRequest request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).body(new ApiResponse(401, "未登录", null));
        }
        
        try {
            boolean success = userPaymentService.updatePaymentPassword(
                userId, 
                request.getCurrentPassword(), 
                request.getNewPassword(), 
                request.getVerificationCode()
            );
            
            if (success) {
                return ResponseEntity.ok(new ApiResponse(200, "支付密码更新成功", null));
            } else {
                return ResponseEntity.ok(new ApiResponse(400, "支付密码更新失败", null));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(400, e.getMessage(), null));
        }
    }
} 