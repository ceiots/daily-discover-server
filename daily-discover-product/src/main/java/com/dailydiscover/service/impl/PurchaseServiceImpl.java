package com.dailydiscover.service.impl;

import com.dailydiscover.mapper.PurchaseMapper;
import com.dailydiscover.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {
    
    private final PurchaseMapper purchaseMapper;
    
    @Override
    public List<Map<String, Object>> getPurchaseHistory() {
        try {
            log.info("获取购买历史");
            
            List<Map<String, Object>> history = purchaseMapper.getPurchaseHistory();
            
            return history;
        } catch (Exception e) {
            log.error("获取购买历史失败", e);
            throw new RuntimeException("获取购买历史失败", e);
        }
    }
    
    @Override
    public List<Map<String, Object>> getPurchaseHistoryByProduct(String productId) {
        try {
            log.info("获取商品购买历史: productId={}", productId);
            
            List<Map<String, Object>> history = purchaseMapper.getPurchaseHistoryByProduct(productId);
            
            return history;
        } catch (Exception e) {
            log.error("获取商品购买历史失败: productId={}", productId, e);
            throw new RuntimeException("获取商品购买历史失败", e);
        }
    }
    
    @Override
    public List<Map<String, Object>> getUserPurchaseHistory(String userId) {
        try {
            log.info("获取用户购买历史: userId={}", userId);
            
            List<Map<String, Object>> history = purchaseMapper.getUserPurchaseHistory(userId);
            
            return history;
        } catch (Exception e) {
            log.error("获取用户购买历史失败: userId={}", userId, e);
            throw new RuntimeException("获取用户购买历史失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getPurchaseStats() {
        try {
            log.info("获取购买统计信息");
            
            Map<String, Object> stats = purchaseMapper.getPurchaseStats();
            
            return stats;
        } catch (Exception e) {
            log.error("获取购买统计信息失败", e);
            throw new RuntimeException("获取购买统计信息失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getProductPurchaseStats(String productId) {
        try {
            log.info("获取商品购买统计: productId={}", productId);
            
            Map<String, Object> stats = purchaseMapper.getProductPurchaseStats(productId);
            
            return stats;
        } catch (Exception e) {
            log.error("获取商品购买统计失败: productId={}", productId, e);
            throw new RuntimeException("获取商品购买统计失败", e);
        }
    }
    
    @Override
    public Map<String, Object> recordPurchase(String productId, String userId, int quantity) {
        try {
            log.info("记录购买记录: productId={}, userId={}, quantity={}", productId, userId, quantity);
            
            Map<String, Object> purchase = new HashMap<>();
            purchase.put("productId", productId);
            purchase.put("userId", userId);
            purchase.put("quantity", quantity);
            purchase.put("timestamp", System.currentTimeMillis());
            
            purchaseMapper.recordPurchase(purchase);
            
            Map<String, Object> result = new HashMap<>();
            result.put("productId", productId);
            result.put("userId", userId);
            result.put("quantity", quantity);
            result.put("timestamp", System.currentTimeMillis());
            result.put("success", true);
            result.put("message", "购买记录已保存");
            
            return result;
        } catch (Exception e) {
            log.error("记录购买记录失败: productId={}, userId={}, quantity={}", productId, userId, quantity, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "记录购买记录失败");
            return errorResult;
        }
    }
}