package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mapper.CartItemMapper;
import com.example.model.CartItem;
import com.example.config.ImageConfig;

@Service
public class CartService {

    @Autowired
    private CartItemMapper cartItemMapper;

    public List<CartItem> getCartItems(Long userId) {
        List<CartItem> items = cartItemMapper.getCartItemsByUserId(userId);
        // 添加动态拼接逻辑
        items.forEach(item -> {
            item.setProductImage(ImageConfig.getFullImageUrl(item.getProductImage()));
            item.setShopAvatarUrl(ImageConfig.getFullImageUrl(item.getShopAvatarUrl()));
        });
        return items;
    }

    public void addCartItem(CartItem cartItem) {
        CartItem existingItem = cartItemMapper.findByUserIdAndProductId(cartItem.getUserId(), cartItem.getProductId());
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
            cartItemMapper.updateCartItemQuantity(existingItem.getId(), existingItem.getQuantity());
        } else {
            // 新增URL处理逻辑
            String cleanImageUrl = cartItem.getProductImage().replaceFirst("^https?://[^/]+\\.r5\\.cpolar\\.top", "");
            cartItem.setProductImage(cleanImageUrl);

            String cleanShopAvatarUrl = cartItem.getShopAvatarUrl().replaceFirst("^https?://[^/]+\\.r5\\.cpolar\\.top", "");
            cartItem.setShopAvatarUrl(cleanShopAvatarUrl);
            cartItemMapper.addCartItem(cartItem);
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