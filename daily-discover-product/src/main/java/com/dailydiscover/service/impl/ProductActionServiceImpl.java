package com.dailydiscover.service.impl;

import com.dailydiscover.mapper.ProductActionMapper;
import com.dailydiscover.model.Product;
import com.dailydiscover.service.ProductActionService;
import com.dailydiscover.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductActionServiceImpl implements ProductActionService {
    
    private final ProductActionMapper productActionMapper;
    private final ProductService productService;
    
    @Override
    public Map<String, Object> toggleCollection(Long productId) {
        try {
            log.info("收藏/取消收藏商品: productId={}", productId);
            
            // 模拟用户ID，实际应该从认证信息中获取
            String userId = "user123";
            
            // 检查是否已经收藏
            boolean isCollected = productActionMapper.isProductCollected(userId, productId);
            
            if (isCollected) {
                // 取消收藏
                productActionMapper.removeFromCollection(userId, productId);
                
                Map<String, Object> result = new HashMap<>();
                result.put("productId", productId);
                result.put("collected", false);
                result.put("success", true);
                result.put("message", "取消收藏成功");
                return result;
            } else {
                // 添加收藏
                productActionMapper.addToCollection(userId, productId);
                
                Map<String, Object> result = new HashMap<>();
                result.put("productId", productId);
                result.put("collected", true);
                result.put("success", true);
                result.put("message", "收藏成功");
                return result;
            }
        } catch (Exception e) {
            log.error("收藏商品失败: productId={}", productId, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "收藏失败");
            return errorResult;
        }
    }
    
    @Override
    public Map<String, Object> getCollectionStatus(Long productId) {
        try {
            log.info("获取商品收藏状态: productId={}", productId);
            
            // 模拟用户ID，实际应该从认证信息中获取
            String userId = "user123";
            
            boolean isCollected = productActionMapper.isProductCollected(userId, productId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("productId", productId);
            result.put("collected", isCollected);
            
            return result;
        } catch (Exception e) {
            log.error("获取收藏状态失败: productId={}", productId, e);
            throw new RuntimeException("获取收藏状态失败", e);
        }
    }
    
    @Override
    public Map<String, Object> shareProduct(Long productId) {
        try {
            log.info("分享商品: productId={}", productId);
            
            Product product = productService.findById(productId);
            
            // 模拟用户ID，实际应该从认证信息中获取
            String userId = "user123";
            
            // 记录分享
            productActionMapper.recordProductShare(userId, productId);
            
            Map<String, Object> result = new HashMap<>();
            if (product != null) {
                result.put("productId", productId);
                result.put("title", product.getTitle());
                result.put("imageUrl", product.getMainImageUrl());
                result.put("shareUrl", "/product/" + productId);
                result.put("success", true);
                return result;
            }
            
            result.put("success", false);
            result.put("message", "商品不存在");
            return result;
        } catch (Exception e) {
            log.error("分享商品失败: productId={}", productId, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "分享失败");
            return errorResult;
        }
    }
    
    @Override
    public Map<String, Object> getShareStats(Long productId) {
        try {
            log.info("获取商品分享统计: productId={}", productId);
            
            Map<String, Object> stats = productActionMapper.getProductShareStats(productId);
            
            return stats;
        } catch (Exception e) {
            log.error("获取分享统计失败: productId={}", productId, e);
            throw new RuntimeException("获取分享统计失败", e);
        }
    }
    
    @Override
    public Map<String, Object> addToViewHistory(Long productId) {
        try {
            log.info("添加商品到浏览历史: productId={}", productId);
            
            // 模拟用户ID，实际应该从认证信息中获取
            String userId = "user123";
            
            productActionMapper.addToViewHistory(userId, productId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("productId", productId);
            result.put("success", true);
            result.put("message", "已添加到浏览历史");
            
            return result;
        } catch (Exception e) {
            log.error("添加浏览历史失败: productId={}", productId, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "添加失败");
            return errorResult;
        }
    }
    
    @Override
    public Map<String, Object> getViewHistory(String userId) {
        try {
            log.info("获取用户浏览历史: userId={}", userId);
            
            Map<String, Object> history = productActionMapper.getUserViewHistory(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("history", history);
            result.put("totalCount", history.size());
            
            return result;
        } catch (Exception e) {
            log.error("获取浏览历史失败: userId={}", userId, e);
            throw new RuntimeException("获取浏览历史失败", e);
        }
    }
}