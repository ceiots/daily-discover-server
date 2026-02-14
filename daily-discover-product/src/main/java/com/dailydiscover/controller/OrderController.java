package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    @PostMapping("/create")
    @ApiLog("创建订单")
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Map<String, Object> request) {
        try {
            String productId = request.get("productId").toString();
            int quantity = Integer.parseInt(request.get("quantity").toString());
            
            Map<String, Object> result = new HashMap<>();
            result.put("orderId", "ORDER" + System.currentTimeMillis());
            result.put("productId", productId);
            result.put("quantity", quantity);
            result.put("success", true);
            result.put("message", "订单创建成功");
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "订单创建失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
}