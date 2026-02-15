package com.dailydiscover.service;

import java.util.Map;

public interface ProductActionService {
    
    /**
     * 收藏/取消收藏商品
     * @param productId 商品ID
     * @return 操作结果
     */
    Map<String, Object> toggleCollection(Long productId);
    
    /**
     * 获取商品收藏状态
     * @param productId 商品ID
     * @return 收藏状态
     */
    Map<String, Object> getCollectionStatus(Long productId);
    
    /**
     * 分享商品
     * @param productId 商品ID
     * @return 分享信息
     */
    Map<String, Object> shareProduct(Long productId);
    
    /**
     * 获取商品分享统计
     * @param productId 商品ID
     * @return 分享统计
     */
    Map<String, Object> getShareStats(Long productId);
    
    /**
     * 添加商品到浏览历史
     * @param productId 商品ID
     * @return 操作结果
     */
    Map<String, Object> addToViewHistory(Long productId);
    
    /**
     * 获取用户浏览历史
     * @param userId 用户ID
     * @return 浏览历史列表
     */
    Map<String, Object> getViewHistory(String userId);
}