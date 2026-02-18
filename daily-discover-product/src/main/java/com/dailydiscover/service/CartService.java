package com.dailydiscover.service;

import com.dailydiscover.model.ShoppingCart;
import java.util.List;
import java.util.Map;

public interface CartService {
    
    /**
     * 根据用户ID查询购物车列表
     */
    List<ShoppingCart> findByUserId(Long userId);
    
    /**
     * 查询用户选中的购物车项
     */
    List<ShoppingCart> findSelectedItemsByUserId(Long userId);
    
    /**
     * 根据用户ID和SKU ID查询购物车项
     */
    ShoppingCart findByUserIdAndSkuId(Long userId, Long skuId);
    
    /**
     * 统计用户购物车商品数量
     */
    Integer countCartItems(Long userId);
    
    /**
     * 加入购物车
     */
    Map<String, Object> addToCart(Long productId, Long skuId, int quantity, String specsJson, String specsText);
    
    /**
     * 获取购物车商品列表
     */
    List<Map<String, Object>> getCartItems();
    
    /**
     * 获取购物车商品详情
     */
    Map<String, Object> getCartItem(Long cartItemId);
    
    /**
     * 获取购物车统计信息
     */
    Map<String, Object> getCartTotal();
    
    /**
     * 更新购物车商品数量
     */
    Map<String, Object> updateCartItem(Long cartItemId, int quantity);
    
    /**
     * 从购物车移除商品
     */
    Map<String, Object> removeFromCart(Long cartItemId);
    
    /**
     * 批量删除购物车项
     */
    Map<String, Object> batchRemoveFromCart(List<Long> cartItemIds);
    
    /**
     * 清空购物车
     */
    Map<String, Object> clearCart();
    
    /**
     * 更新购物车项选中状态
     */
    Map<String, Object> updateCartItemSelection(Long cartItemId, Integer isSelected);
    
    /**
     * 批量更新购物车项选中状态
     */
    Map<String, Object> batchUpdateCartItemSelection(List<Long> cartItemIds, Integer isSelected);
    
    /**
     * 获取选中的购物车项
     */
    List<Map<String, Object>> getSelectedCartItems();
    
    /**
     * 合并购物车
     */
    Map<String, Object> mergeCart(List<Map<String, Object>> tempCartItems);
}