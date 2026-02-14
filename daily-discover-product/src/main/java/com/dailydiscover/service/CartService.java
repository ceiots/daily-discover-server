package com.dailydiscover.service;

import java.util.Map;

public interface CartService {
    
    /**
     * 添加商品到购物车
     * @param productId 商品ID
     * @param quantity 数量
     * @return 操作结果
     */
    Map<String, Object> addToCart(String productId, int quantity);
    
    /**
     * 获取购物车商品数量
     * @param productId 商品ID
     * @return 商品数量信息
     */
    Map<String, Object> getCartItem(String productId);
    
    /**
     * 获取购物车总数量
     * @return 购物车统计信息
     */
    Map<String, Object> getCartTotal();
    
    /**
     * 更新购物车商品数量
     * @param productId 商品ID
     * @param quantity 新数量
     * @return 操作结果
     */
    Map<String, Object> updateCartItem(String productId, int quantity);
    
    /**
     * 从购物车移除商品
     * @param productId 商品ID
     * @return 操作结果
     */
    Map<String, Object> removeFromCart(String productId);
    
    /**
     * 清空购物车
     * @return 操作结果
     */
    Map<String, Object> clearCart();
}