package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mapper.CartItemMapper;
import com.example.model.CartItem;
import com.example.config.ImageConfig;
import com.example.common.api.CommonResult;
import com.example.dao.ProductDao;

import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CartService {
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    @Autowired
    private CartItemMapper cartItemMapper;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private StockService stockService;

    @Transactional
    public List<CartItem> getCartItems(Long userId) {
        List<CartItem> items = cartItemMapper.getCartItemsByUserId(userId);
        items.forEach(item -> {
            item.setProductImage(ImageConfig.getFullImageUrl(item.getProductImage()));
            item.setShopAvatarUrl(ImageConfig.getFullImageUrl(item.getShopAvatarUrl()));
        });
        return items;
    }


    @Transactional
    public CommonResult<?> addCartItem(CartItem cartItem) {
        try {
            if (cartItem == null) {
                logger.warn("Attempted to add a null cart item.");
                return CommonResult.failed("购物车商品不能为空");
            }
            
            // 1. 检查库存是否足够
            Integer stock = stockService.getStock(cartItem.getProductId());
            if (stock < cartItem.getQuantity()) {
                logger.warn("商品库存不足: productId={}, stock={}, quantity={}", 
                        cartItem.getProductId(), stock, cartItem.getQuantity());
                return CommonResult.failed("商品库存不足，剩余" + stock + "件");
            }
            
            
            // 2. 检查是否已存在相同商品（同规格）
            List<CartItem> existingItems = cartItemMapper.findByUserIdAndProductId(cartItem.getUserId(), cartItem.getProductId());
            
            CartItem existingItem = null;
            for (CartItem item : existingItems) {
                if (item.getSpecifications().equals(cartItem.getSpecifications())) {
                    existingItem = item;
                    break;
                }
            }
            
            // 3. 如果已存在，则更新数量
            if (existingItem != null) {
                int newQuantity = existingItem.getQuantity() + cartItem.getQuantity();
                
                // 再次检查合并后的数量是否超过库存
                if (newQuantity > stock) {
                    logger.warn("合并后商品数量超过库存: productId={}, stock={}, newQuantity={}", 
                            cartItem.getProductId(), stock, newQuantity);
                    return CommonResult.failed("商品库存不足，剩余" + stock + "件");
                }
                
                cartItemMapper.updateCartItemQuantity(existingItem.getId(), newQuantity);
                logger.info("Updated cart item quantity with product ID: {} to {}", cartItem.getProductId(), newQuantity);
            } else {
                // 5. 如果商品不存在，新增商品到购物车
                cartItemMapper.addCartItem(cartItem);
                logger.info("Added cart item with product ID: {}", cartItem.getProductId());
            }
            
            return CommonResult.success(null);
        } catch (Exception e) {
            logger.error("加入购物车失败", e);
            return CommonResult.failed("加入购物车失败: " + e.getMessage());
        }
    }

    private void cleanImageUrls(CartItem cartItem) {
        if (cartItem.getProductImage() != null) {
            String cleanImageUrl = cartItem.getProductImage().replace(ImageConfig.getImagePrefix(), "");
            cartItem.setProductImage(cleanImageUrl);
        }
        
        if (cartItem.getShopAvatarUrl() != null) {
            String cleanShopAvatarUrl = cartItem.getShopAvatarUrl().replace(ImageConfig.getImagePrefix(), "");
            cartItem.setShopAvatarUrl(cleanShopAvatarUrl);
        }
    }

    public void updateCartItemQuantity(Long itemId, int quantity) {
        // Update the quantity
        cartItemMapper.updateCartItemQuantity(itemId, quantity);
    }

    public void deleteCartItem(Long itemId) {
        // Remove the item from the cart
        cartItemMapper.deleteCartItem(itemId);
    }
    public int getCartItemCount(Long userId) {
        return cartItemMapper.getCartItemsByUserId(userId).size();
    }
}
