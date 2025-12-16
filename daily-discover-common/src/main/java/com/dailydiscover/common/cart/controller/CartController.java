package com.dailydiscover.common.cart.controller;

import com.dailydiscover.common.result.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车控制器
 */
@Slf4j
@RestController
@RequestMapping("/common/api/cart")
@RequiredArgsConstructor
public class CartController {

    // 模拟购物车存储
    private final Map<String, List<Map<String, Object>>> userCarts = new HashMap<>();

    /**
     * 获取用户购物车
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Result<Map<String, Object>>> getCart(@PathVariable String userId) {
        try {
            log.debug("获取用户购物车: {}", userId);
            
            List<Map<String, Object>> cartItems = userCarts.getOrDefault(userId, new ArrayList<>());
            
            Map<String, Object> cart = new HashMap<>();
            cart.put("userId", userId);
            cart.put("items", cartItems);
            cart.put("totalItems", cartItems.size());
            cart.put("totalAmount", calculateTotalAmount(cartItems));
            
            return ResponseEntity.ok(Result.success(cart));
        } catch (Exception e) {
            log.error("获取购物车失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 添加商品到购物车
     */
    @PostMapping("/{userId}/add")
    public ResponseEntity<Result<Map<String, Object>>> addToCart(@PathVariable String userId, 
                                                                @RequestBody Map<String, Object> item) {
        try {
            log.info("添加商品到购物车: userId={}, productId={}", userId, item.get("productId"));
            
            List<Map<String, Object>> cartItems = userCarts.computeIfAbsent(userId, k -> new ArrayList<>());
            
            // 检查是否已存在相同商品
            boolean exists = cartItems.stream()
                    .anyMatch(cartItem -> cartItem.get("productId").equals(item.get("productId")));
            
            if (exists) {
                // 更新数量
                cartItems.forEach(cartItem -> {
                    if (cartItem.get("productId").equals(item.get("productId"))) {
                        int quantity = (int) cartItem.getOrDefault("quantity", 1);
                        cartItem.put("quantity", quantity + 1);
                    }
                });
            } else {
                // 添加新商品
                item.put("quantity", item.getOrDefault("quantity", 1));
                cartItems.add(item);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("totalItems", cartItems.size());
            result.put("message", "商品添加成功");
            
            log.info("商品添加成功: userId={}, 商品数量={}", userId, cartItems.size());
            return ResponseEntity.ok(Result.success(result));
        } catch (Exception e) {
            log.error("添加商品失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 从购物车移除商品
     */
    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<Result<Map<String, Object>>> removeFromCart(@PathVariable String userId, 
                                                                      @PathVariable String productId) {
        try {
            log.info("从购物车移除商品: userId={}, productId={}", userId, productId);
            
            List<Map<String, Object>> cartItems = userCarts.get(userId);
            if (cartItems != null) {
                cartItems.removeIf(item -> item.get("productId").equals(productId));
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("productId", productId);
            result.put("message", "商品移除成功");
            
            log.info("商品移除成功: userId={}, productId={}", userId, productId);
            return ResponseEntity.ok(Result.success(result));
        } catch (Exception e) {
            log.error("移除商品失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Result<Map<String, Object>>> clearCart(@PathVariable String userId) {
        try {
            log.info("清空购物车: {}", userId);
            
            userCarts.remove(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("message", "购物车已清空");
            
            log.info("购物车清空成功: {}", userId);
            return ResponseEntity.ok(Result.success(result));
        } catch (Exception e) {
            log.error("清空购物车失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Result.error(e.getMessage()));
        }
    }

    private double calculateTotalAmount(List<Map<String, Object>> items) {
        return items.stream()
                .mapToDouble(item -> {
                    double price = (double) item.getOrDefault("price", 0.0);
                    int quantity = (int) item.getOrDefault("quantity", 1);
                    return price * quantity;
                })
                .sum();
    }
}