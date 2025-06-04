package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mapper.CartItemMapper;
import com.example.model.CartItem;
import com.example.config.ImageConfig;

import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CartService {
    private static final Logger logger = LoggerFactory.getLogger(CartService.class);

    @Autowired
    private CartItemMapper cartItemMapper;

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
    public void addCartItem(CartItem cartItem) {
        if (cartItem == null) {
            logger.warn("Attempted to add a null cart item.");
            return;
        }

        // 先根据user_id和product_id查询所有符合条件的商品
        List<CartItem> existingItems = cartItemMapper.findByUserIdAndProductId(cartItem.getUserId(), cartItem.getProductId());
        System.out.println("existingItems: " + existingItems);
        CartItem existingItem = null;
        for (CartItem item : existingItems) {
            if (item.getSpecifications().equals(cartItem.getSpecifications())) {
                existingItem = item;
                break;
            }
        }

        if (existingItem != null) {
            // 如果商品已存在，更新商品数量
            int newQuantity = existingItem.getQuantity() + cartItem.getQuantity();
            cartItemMapper.updateCartItemQuantity(existingItem.getId(), newQuantity);
            logger.info("Updated cart item quantity with product ID: {} to {}", cartItem.getProductId(), newQuantity);
        } else {
            // 如果商品不存在，新增商品到购物车
            cartItemMapper.addCartItem(cartItem);
            logger.info("Added cart item with product ID: {}", cartItem.getProductId());
        }
    }

    private void cleanImageUrls(CartItem cartItem) {
        String cleanImageUrl = cartItem.getProductImage().replace(ImageConfig.getImagePrefix(), "");
        cartItem.setProductImage(cleanImageUrl);

        String cleanShopAvatarUrl = cartItem.getShopAvatarUrl().replace(ImageConfig.getImagePrefix(), "");
        cartItem.setShopAvatarUrl(cleanShopAvatarUrl);
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
