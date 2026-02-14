package com.dailydiscover.service;

import java.util.List;
import java.util.Map;

public interface PurchaseService {
    
    /**
     * 获取购买历史
     * @return 购买历史列表
     */
    List<Map<String, Object>> getPurchaseHistory();
    
    /**
     * 根据商品获取购买历史
     * @param productId 商品ID
     * @return 购买历史列表
     */
    List<Map<String, Object>> getPurchaseHistoryByProduct(String productId);
    
    /**
     * 获取用户购买历史
     * @param userId 用户ID
     * @return 购买历史列表
     */
    List<Map<String, Object>> getUserPurchaseHistory(String userId);
    
    /**
     * 获取购买统计信息
     * @return 统计信息
     */
    Map<String, Object> getPurchaseStats();
    
    /**
     * 获取商品购买统计
     * @param productId 商品ID
     * @return 购买统计
     */
    Map<String, Object> getProductPurchaseStats(String productId);
    
    /**
     * 记录购买记录
     * @param productId 商品ID
     * @param userId 用户ID
     * @param quantity 数量
     * @return 记录结果
     */
    Map<String, Object> recordPurchase(String productId, String userId, int quantity);
}