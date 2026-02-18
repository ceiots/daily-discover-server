package com.dailydiscover.service.impl;

import com.dailydiscover.common.security.UserContext;
import com.dailydiscover.mapper.CartMapper;
import com.dailydiscover.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    
    private final CartMapper cartMapper;
    private final UserContext userContext;
    
    @Override
    @Transactional
    public Map<String, Object> addToCart(Long productId, Long skuId, int quantity, String specsJson, String specsText) {
        try {
            Long userId = userContext.getCurrentUserIdOrThrow();
            log.info("添加商品到购物车: userId={}, productId={}, skuId={}, quantity={}", userId, productId, skuId, quantity);
            
            // 检查是否已存在相同SKU的购物车项
            Map<String, Object> existingItem = cartMapper.getCartItemBySku(userId, skuId);
            
            if (existingItem != null) {
                // 如果已存在，更新数量
                int newQuantity = (Integer) existingItem.get("quantity") + quantity;
                cartMapper.updateCartItemQuantity((Long) existingItem.get("id"), newQuantity);
                
                Map<String, Object> result = new HashMap<>();
                result.put("cartItemId", existingItem.get("id"));
                result.put("productId", productId);
                result.put("skuId", skuId);
                result.put("quantity", newQuantity);
                result.put("success", true);
                result.put("message", "购物车商品数量已更新");
                return result;
            } else {
                // 如果不存在，新增购物车项
                Map<String, Object> cartItem = new HashMap<>();
                cartItem.put("userId", userId);
                cartItem.put("productId", productId);
                cartItem.put("skuId", skuId);
                cartItem.put("quantity", quantity);
                cartItem.put("specsJson", specsJson);
                cartItem.put("specsText", specsText);
                cartItem.put("isSelected", 1);
                
                cartMapper.addToCart(cartItem);
                
                Map<String, Object> result = new HashMap<>();
                result.put("cartItemId", cartItem.get("id"));
                result.put("productId", productId);
                result.put("skuId", skuId);
                result.put("quantity", quantity);
                result.put("success", true);
                result.put("message", "商品已加入购物车");
                return result;
            }
        } catch (Exception e) {
            log.error("添加购物车失败: productId={}, skuId={}, quantity={}", productId, skuId, quantity, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "加入购物车失败");
            return errorResult;
        }
    }
    
    @Override
    public List<Map<String, Object>> getCartItems() {
        try {
            Long userId = userContext.getCurrentUserIdOrThrow();
            log.info("获取购物车商品列表: userId={}", userId);
            
            List<Map<String, Object>> cartItems = cartMapper.getCartItems(userId);
            
            // 添加商品信息（这里需要调用商品服务获取商品详情）
            return cartItems.stream().map(item -> {
                Map<String, Object> cartItemWithProduct = new HashMap<>(item);
                // TODO: 调用商品服务获取商品详情
                cartItemWithProduct.put("productInfo", new HashMap<>());
                return cartItemWithProduct;
            }).collect(Collectors.toList());
            
        } catch (Exception e) {
            log.error("获取购物车商品列表失败", e);
            throw new RuntimeException("获取购物车商品列表失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getCartItem(Long cartItemId) {
        try {
            Long userId = userContext.getCurrentUserIdOrThrow();
            log.info("获取购物车商品: userId={}, cartItemId={}", userId, cartItemId);
            
            Map<String, Object> cartItem = cartMapper.getCartItemById(cartItemId);
            
            if (cartItem == null) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("message", "购物车项不存在");
                return errorResult;
            }
            
            // 验证用户权限
            if (!cartItem.get("user_id").equals(userId)) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("message", "无权限访问该购物车项");
                return errorResult;
            }
            
            Map<String, Object> result = new HashMap<>(cartItem);
            result.put("success", true);
            return result;
            
        } catch (Exception e) {
            log.error("获取购物车商品失败: cartItemId={}", cartItemId, e);
            throw new RuntimeException("获取购物车商品失败", e);
        }
    }
    
    @Override
    public Map<String, Object> getCartTotal() {
        try {
            Long userId = userContext.getCurrentUserIdOrThrow();
            log.info("获取购物车统计信息: userId={}", userId);
            
            Integer totalCount = cartMapper.getCartTotalCount(userId);
            Integer totalQuantity = cartMapper.getCartTotalQuantity(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalCount", totalCount != null ? totalCount : 0);
            result.put("totalQuantity", totalQuantity != null ? totalQuantity : 0);
            result.put("success", true);
            
            return result;
        } catch (Exception e) {
            log.error("获取购物车统计信息失败", e);
            throw new RuntimeException("获取购物车统计信息失败", e);
        }
    }
    
    @Override
    public Map<String, Object> updateCartItem(Long cartItemId, int quantity) {
        try {
            Long userId = userContext.getCurrentUserIdOrThrow();
            log.info("更新购物车商品数量: userId={}, cartItemId={}, quantity={}", userId, cartItemId, quantity);
            
            // 验证购物车项是否存在且属于当前用户
            Map<String, Object> cartItem = cartMapper.getCartItemById(cartItemId);
            if (cartItem == null || !cartItem.get("user_id").equals(userId)) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("message", "购物车项不存在或无权限");
                return errorResult;
            }
            
            cartMapper.updateCartItemQuantity(cartItemId, quantity);
            
            Map<String, Object> result = new HashMap<>();
            result.put("cartItemId", cartItemId);
            result.put("quantity", quantity);
            result.put("success", true);
            result.put("message", "购物车商品数量已更新");
            
            return result;
        } catch (Exception e) {
            log.error("更新购物车商品失败: cartItemId={}, quantity={}", cartItemId, quantity, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "更新购物车商品失败");
            return errorResult;
        }
    }
    
    @Override
    public Map<String, Object> removeFromCart(Long cartItemId) {
        try {
            Long userId = userContext.getCurrentUserIdOrThrow();
            log.info("从购物车移除商品: userId={}, cartItemId={}", userId, cartItemId);
            
            // 验证购物车项是否存在且属于当前用户
            Map<String, Object> cartItem = cartMapper.getCartItemById(cartItemId);
            if (cartItem == null || !cartItem.get("user_id").equals(userId)) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("message", "购物车项不存在或无权限");
                return errorResult;
            }
            
            cartMapper.removeFromCart(cartItemId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("cartItemId", cartItemId);
            result.put("success", true);
            result.put("message", "商品已从购物车移除");
            
            return result;
        } catch (Exception e) {
            log.error("移除购物车商品失败: cartItemId={}", cartItemId, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "移除购物车商品失败");
            return errorResult;
        }
    }
    
    @Override
    @Transactional
    public Map<String, Object> batchRemoveFromCart(List<Long> cartItemIds) {
        try {
            Long userId = userContext.getCurrentUserIdOrThrow();
            log.info("批量删除购物车项: userId={}, cartItemIds={}", userId, cartItemIds);
            
            if (cartItemIds == null || cartItemIds.isEmpty()) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("message", "购物车项ID列表不能为空");
                return errorResult;
            }
            
            // 验证所有购物车项是否属于当前用户
            for (Long cartItemId : cartItemIds) {
                Map<String, Object> cartItem = cartMapper.getCartItemById(cartItemId);
                if (cartItem == null || !cartItem.get("user_id").equals(userId)) {
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("success", false);
                    errorResult.put("message", "购物车项不存在或无权限");
                    return errorResult;
                }
            }
            
            cartMapper.batchRemoveFromCart(cartItemIds);
            
            Map<String, Object> result = new HashMap<>();
            result.put("removedCount", cartItemIds.size());
            result.put("success", true);
            result.put("message", "批量删除购物车项成功");
            
            return result;
        } catch (Exception e) {
            log.error("批量删除购物车项失败: cartItemIds={}", cartItemIds, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "批量删除购物车项失败");
            return errorResult;
        }
    }
    
    @Override
    public Map<String, Object> clearCart() {
        try {
            Long userId = userContext.getCurrentUserIdOrThrow();
            log.info("清空购物车: userId={}", userId);
            
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
    
    @Override
    public Map<String, Object> updateCartItemSelection(Long cartItemId, Integer isSelected) {
        try {
            Long userId = userContext.getCurrentUserIdOrThrow();
            log.info("更新购物车项选中状态: userId={}, cartItemId={}, isSelected={}", userId, cartItemId, isSelected);
            
            // 验证购物车项是否存在且属于当前用户
            Map<String, Object> cartItem = cartMapper.getCartItemById(cartItemId);
            if (cartItem == null || !cartItem.get("user_id").equals(userId)) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("message", "购物车项不存在或无权限");
                return errorResult;
            }
            
            cartMapper.updateCartItemSelection(cartItemId, isSelected);
            
            Map<String, Object> result = new HashMap<>();
            result.put("cartItemId", cartItemId);
            result.put("isSelected", isSelected);
            result.put("success", true);
            result.put("message", "购物车项选中状态已更新");
            
            return result;
        } catch (Exception e) {
            log.error("更新购物车项选中状态失败: cartItemId={}, isSelected={}", cartItemId, isSelected, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "更新购物车项选中状态失败");
            return errorResult;
        }
    }
    
    @Override
    @Transactional
    public Map<String, Object> batchUpdateCartItemSelection(List<Long> cartItemIds, Integer isSelected) {
        try {
            Long userId = userContext.getCurrentUserIdOrThrow();
            log.info("批量更新购物车项选中状态: userId={}, cartItemIds={}, isSelected={}", userId, cartItemIds, isSelected);
            
            if (cartItemIds == null || cartItemIds.isEmpty()) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("message", "购物车项ID列表不能为空");
                return errorResult;
            }
            
            // 验证所有购物车项是否属于当前用户
            for (Long cartItemId : cartItemIds) {
                Map<String, Object> cartItem = cartMapper.getCartItemById(cartItemId);
                if (cartItem == null || !cartItem.get("user_id").equals(userId)) {
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("success", false);
                    errorResult.put("message", "购物车项不存在或无权限");
                    return errorResult;
                }
            }
            
            cartMapper.batchUpdateCartItemSelection(cartItemIds, isSelected);
            
            Map<String, Object> result = new HashMap<>();
            result.put("updatedCount", cartItemIds.size());
            result.put("isSelected", isSelected);
            result.put("success", true);
            result.put("message", "批量更新购物车项选中状态成功");
            
            return result;
        } catch (Exception e) {
            log.error("批量更新购物车项选中状态失败: cartItemIds={}, isSelected={}", cartItemIds, isSelected, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "批量更新购物车项选中状态失败");
            return errorResult;
        }
    }
    
    @Override
    public List<Map<String, Object>> getSelectedCartItems() {
        try {
            Long userId = userContext.getCurrentUserIdOrThrow();
            log.info("获取选中的购物车项: userId={}", userId);
            
            List<Map<String, Object>> selectedItems = cartMapper.getSelectedCartItems(userId);
            
            // 添加商品信息
            return selectedItems.stream().map(item -> {
                Map<String, Object> cartItemWithProduct = new HashMap<>(item);
                // TODO: 调用商品服务获取商品详情
                cartItemWithProduct.put("productInfo", new HashMap<>());
                return cartItemWithProduct;
            }).collect(Collectors.toList());
            
        } catch (Exception e) {
            log.error("获取选中的购物车项失败", e);
            throw new RuntimeException("获取选中的购物车项失败", e);
        }
    }
    
    @Override
    @Transactional
    public Map<String, Object> mergeCart(List<Map<String, Object>> tempCartItems) {
        try {
            Long userId = userContext.getCurrentUserIdOrThrow();
            log.info("合并购物车: userId={}, tempCartItemsCount={}", userId, tempCartItems.size());
            
            if (tempCartItems == null || tempCartItems.isEmpty()) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "无需合并购物车");
                return result;
            }
            
            int mergedCount = 0;
            
            for (Map<String, Object> tempItem : tempCartItems) {
                Long productId = Long.parseLong(tempItem.get("productId").toString());
                Long skuId = Long.parseLong(tempItem.get("skuId").toString());
                int quantity = Integer.parseInt(tempItem.get("quantity").toString());
                String specsJson = (String) tempItem.get("specsJson");
                String specsText = (String) tempItem.get("specsText");
                
                // 检查是否已存在相同SKU的购物车项
                Map<String, Object> existingItem = cartMapper.getCartItemBySku(userId, skuId);
                
                if (existingItem != null) {
                    // 如果已存在，更新数量
                    int newQuantity = (Integer) existingItem.get("quantity") + quantity;
                    cartMapper.updateCartItemQuantity((Long) existingItem.get("id"), newQuantity);
                    mergedCount++;
                } else {
                    // 如果不存在，新增购物车项
                    Map<String, Object> cartItem = new HashMap<>();
                    cartItem.put("userId", userId);
                    cartItem.put("productId", productId);
                    cartItem.put("skuId", skuId);
                    cartItem.put("quantity", quantity);
                    cartItem.put("specsJson", specsJson);
                    cartItem.put("specsText", specsText);
                    cartItem.put("isSelected", 1);
                    
                    cartMapper.addToCart(cartItem);
                    mergedCount++;
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("mergedCount", mergedCount);
            result.put("success", true);
            result.put("message", "购物车合并成功");
            
            return result;
        } catch (Exception e) {
            log.error("合并购物车失败", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "合并购物车失败");
            return errorResult;
        }
    }
}