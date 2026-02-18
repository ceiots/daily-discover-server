package com.dailydiscover.service;


import java.util.List;
import java.util.Map;

public interface CartService {
    
    // 基础购物车操作（支持SKU规格）
    /**
     * 添加商品到购物车（支持SKU规格选择）
     * @param productId 商品ID
     * @param skuId SKU ID（对应具体规格）
     * @param quantity 数量
     * @param specsJson 规格组合JSON
     * @param specsText 规格文本
     * @return 操作结果
     */
    Map<String, Object> addToCart(Long productId, Long skuId, int quantity, String specsJson, String specsText);
    
    /**
     * 获取购物车商品列表
     * @return 购物车商品列表
     */
    List<Map<String, Object>> getCartItems();
    
    /**
     * 获取购物车商品数量
     * @param cartItemId 购物车项ID
     * @return 商品数量信息
     */
    Map<String, Object> getCartItem(Long cartItemId);
    
    /**
     * 获取购物车总数量
     * @return 购物车统计信息
     */
    Map<String, Object> getCartTotal();
    
    /**
     * 更新购物车商品数量
     * @param cartItemId 购物车项ID
     * @param quantity 新数量
     * @return 操作结果
     */
    Map<String, Object> updateCartItem(Long cartItemId, int quantity);
    
    /**
     * 从购物车移除商品
     * @param cartItemId 购物车项ID
     * @return 操作结果
     */
    Map<String, Object> removeFromCart(Long cartItemId);
    
    /**
     * 批量删除购物车项
     * @param cartItemIds 购物车项ID列表
     * @return 操作结果
     */
    Map<String, Object> batchRemoveFromCart(List<Long> cartItemIds);
    
    /**
     * 清空购物车
     * @return 操作结果
     */
    Map<String, Object> clearCart();
    
    /**
     * 更新购物车项选中状态
     * @param cartItemId 购物车项ID
     * @param isSelected 是否选中：0-未选中 1-选中
     * @return 操作结果
     */
    Map<String, Object> updateCartItemSelection(Long cartItemId, Integer isSelected);
    
    /**
     * 批量更新购物车项选中状态
     * @param cartItemIds 购物车项ID列表
     * @param isSelected 是否选中：0-未选中 1-选中
     * @return 操作结果
     */
    Map<String, Object> batchUpdateCartItemSelection(List<Long> cartItemIds, Integer isSelected);
    
    /**
     * 获取选中的购物车项
     * @return 选中的购物车项列表
     */
    List<Map<String, Object>> getSelectedCartItems();
    
    /**
     * 合并购物车（用户登录后合并临时购物车）
     * @param tempCartItems 临时购物车项列表
     * @return 合并结果
     */
    Map<String, Object> mergeCart(List<Map<String, Object>> tempCartItems);
}