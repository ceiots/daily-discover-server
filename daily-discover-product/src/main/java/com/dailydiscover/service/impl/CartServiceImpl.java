package com.dailydiscover.service.impl;

import com.dailydiscover.mapper.CartMapper;
import com.dailydiscover.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    
    private final CartMapper cartMapper;
    
    @Override
    public Map<String, Object> addToCart(String productId, int quantity) {
        try {
            log.info("添加商品到购物车: productId={}, quantity={}", productId, quantity);
            
            // 模拟用户ID，实际应该从认证信息中获取
            String userId = "user123";
            
            Map<String, Object> cartItem = new HashMap<>();
            cartItem.put("userId", userId);
            cartItem.put("productId", productId);
            cartItem.put("quantity", quantity);
            
            cartMapper.addToCart(cartItem);
            
            Map<String, Object> result = new HashMap<>();
            result.put("productId", productId);
            result.put("quantity", quantity);
            result.put("success", true);
            result.put("message", "商品已加入购物车");
            
            return result;
        } catch (Exception e) {
            log.error("添加购物车失败: productId={}, quantity={}", productId, quantity, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "加入购物车失败");
            return errorResult;
        }
    }
    
    @Override
    public Map<String, Object> getCartItem(String productId) {
        try {
            log.info("获取购物车商品数量: productId={}", productId);
            
            // 模拟用户ID，实际应该从认证信息中获取
            String userId = "user123";
            
            Integer quantity = cartMapper.getCartItemQuantity(userId, productId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("productId", productId);
            result.put("quantity", quantity != null ? quantity : 0);
            
            return result;
        } catch (Exception e) {
            log.error("获取购物车商品失败: productId={}", productId, e);
            throw new RuntimeException("获取购物车商品失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getCartTotal() {
        try {
            log.info("获取购物车总数量");
            
            // 模拟用户ID，实际应该从认证信息中获取
            String userId = "user123";
            
            Integer totalCount = cartMapper.getCartTotalCount(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalCount", totalCount != null ? totalCount : 0);
            
            return result;
        } catch (Exception e) {
            log.error("获取购物车总数失败", e);
            throw new RuntimeException("获取购物车总数失败", e);
        }
    }
    
    @Override
    public Map<String, Object> updateCartItem(String productId, int quantity) {
        try {
            log.info("更新购物车商品数量: productId={}, quantity={}", productId, quantity);
            
            // 模拟用户ID，实际应该从认证信息中获取
            String userId = "user123";
            
            Map<String, Object> cartItem = new HashMap<>();
            cartItem.put("userId", userId);
            cartItem.put("productId", productId);
            cartItem.put("quantity", quantity);
            
            cartMapper.updateCartItemQuantity(cartItem);
            
            Map<String, Object> result = new HashMap<>();
            result.put("productId", productId);
            result.put("quantity", quantity);
            result.put("success", true);
            result.put("message", "购物车商品数量已更新");
            
            return result;
        } catch (Exception e) {
            log.error("更新购物车商品失败: productId={}, quantity={}", productId, quantity, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "更新购物车商品失败");
            return errorResult;
        }
    }
    
    @Override
    public Map<String, Object> removeFromCart(String productId) {
        try {
            log.info("从购物车移除商品: productId={}", productId);
            
            // 模拟用户ID，实际应该从认证信息中获取
            String userId = "user123";
            
            cartMapper.removeFromCart(userId, productId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("productId", productId);
            result.put("success", true);
            result.put("message", "商品已从购物车移除");
            
            return result;
        } catch (Exception e) {
            log.error("移除购物车商品失败: productId={}", productId, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "移除购物车商品失败");
            return errorResult;
        }
    }
    
    @Override
    public Map<String, Object> clearCart() {
        try {
            log.info("清空购物车");
            
            // 模拟用户ID，实际应该从认证信息中获取
            String userId = "user123";
            
            cartMapper.clearCart(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "购物车已清空");
            
            return result;
        } catch (Exception e) {
            log.error("清空购物车失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "清空购物车失败");
            return errorResult;
        }
    }
}