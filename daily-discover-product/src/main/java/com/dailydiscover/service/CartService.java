package com.dailydiscover.service;

import com.dailydiscover.model.ShoppingCart;
import java.util.List;

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
}