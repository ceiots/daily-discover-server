package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    @ApiLog("加入购物车（支持SKU规格）")
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody Map<String, Object> request) {
        try {
            Long productId = Long.parseLong(request.get("productId").toString());
            Long skuId = Long.parseLong(request.get("skuId").toString());
            int quantity = Integer.parseInt(request.get("quantity").toString());
            String specsJson = (String) request.get("specsJson");
            String specsText = (String) request.get("specsText");
            
            Map<String, Object> result = cartService.addToCart(productId, skuId, quantity, specsJson, specsText);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "加入购物车失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    
    @GetMapping("/items")
    @ApiLog("获取购物车商品列表")
    public ResponseEntity<List<Map<String, Object>>> getCartItems() {
        try {
            List<Map<String, Object>> result = cartService.getCartItems();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/item/{cartItemId}")
    @ApiLog("获取购物车商品详情")
    public ResponseEntity<Map<String, Object>> getCartItem(@PathVariable Long cartItemId) {
        try {
            Map<String, Object> result = cartService.getCartItem(cartItemId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/total")
    @ApiLog("获取购物车统计信息")
    public ResponseEntity<Map<String, Object>> getCartTotal() {
        try {
            Map<String, Object> result = cartService.getCartTotal();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/update-quantity")
    @ApiLog("更新购物车商品数量")
    public ResponseEntity<Map<String, Object>> updateCartItem(@RequestBody Map<String, Object> request) {
        try {
            Long cartItemId = Long.parseLong(request.get("cartItemId").toString());
            int quantity = Integer.parseInt(request.get("quantity").toString());
            
            Map<String, Object> result = cartService.updateCartItem(cartItemId, quantity);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "更新购物车商品失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    
    @DeleteMapping("/remove/{cartItemId}")
    @ApiLog("从购物车移除商品")
    public ResponseEntity<Map<String, Object>> removeFromCart(@PathVariable Long cartItemId) {
        try {
            Map<String, Object> result = cartService.removeFromCart(cartItemId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "移除购物车商品失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    
    @PostMapping("/batch-remove")
    @ApiLog("批量删除购物车项")
    public ResponseEntity<Map<String, Object>> batchRemoveFromCart(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> cartItemIds = (List<Long>) request.get("cartItemIds");
            Map<String, Object> result = cartService.batchRemoveFromCart(cartItemIds);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "批量删除购物车项失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    
    @DeleteMapping("/clear")
    @ApiLog("清空购物车")
    public ResponseEntity<Map<String, Object>> clearCart() {
        try {
            Map<String, Object> result = cartService.clearCart();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "清空购物车失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    
    @PutMapping("/update-selection")
    @ApiLog("更新购物车项选中状态")
    public ResponseEntity<Map<String, Object>> updateCartItemSelection(@RequestBody Map<String, Object> request) {
        try {
            Long cartItemId = Long.parseLong(request.get("cartItemId").toString());
            Integer isSelected = Integer.parseInt(request.get("isSelected").toString());
            
            Map<String, Object> result = cartService.updateCartItemSelection(cartItemId, isSelected);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "更新购物车项选中状态失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    
    @PostMapping("/batch-update-selection")
    @ApiLog("批量更新购物车项选中状态")
    public ResponseEntity<Map<String, Object>> batchUpdateCartItemSelection(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> cartItemIds = (List<Long>) request.get("cartItemIds");
            Integer isSelected = Integer.parseInt(request.get("isSelected").toString());
            
            Map<String, Object> result = cartService.batchUpdateCartItemSelection(cartItemIds, isSelected);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "Failed to batch update cart item selection");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    
    @GetMapping("/selected")
    @ApiLog("获取选中的购物车项")
    public ResponseEntity<List<Map<String, Object>>> getSelectedCartItems() {
        try {
            List<Map<String, Object>> result = cartService.getSelectedCartItems();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/merge")
    @ApiLog("合并购物车")
    public ResponseEntity<Map<String, Object>> mergeCart(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> tempCartItems = (List<Map<String, Object>>) request.get("tempCartItems");
            Map<String, Object> result = cartService.mergeCart(tempCartItems);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "Failed to merge cart");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
}