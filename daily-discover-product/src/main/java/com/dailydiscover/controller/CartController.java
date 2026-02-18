package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    @ApiLog("加入购物车")
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody Map<String, Object> request) {
        try {
            Long productId = Long.parseLong(request.get("productId").toString());
            int quantity = Integer.parseInt(request.get("quantity").toString());
            
            Map<String, Object> result = cartService.addToCart(productId, quantity);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "加入购物车失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    
    @GetMapping("/item/{productId}")
    @ApiLog("获取购物车商品数量")
    public ResponseEntity<Map<String, Object>> getCartItem(@PathVariable Long productId) {
        try {
            Map<String, Object> result = cartService.getCartItem(productId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/total")
    @ApiLog("获取购物车总数量")
    public ResponseEntity<Map<String, Object>> getCartTotal() {
        try {
            Map<String, Object> result = cartService.getCartTotal();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}