package com.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.mapper.CartItemMapper;
import com.example.model.CartItem;

@Service
public class CartService {

    @Autowired
    private CartItemMapper cartItemMapper;

    public List<CartItem> getCartItems(Long userId) {
        return cartItemMapper.getCartItemsByUserId(userId);
    }

    public void addCartItem(CartItem cartItem) {
        // Check if the item already exists in the cart
        CartItem existingItem = cartItemMapper.findByUserIdAndProductId(cartItem.getUserId(), cartItem.getProductId());
        if (existingItem != null) {
            // If it exists, increase the quantity
            existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
            cartItemMapper.updateCartItemQuantity(existingItem.getId(), existingItem.getQuantity());
        } else {
            /* cartItem.setProduct_image(cartItem.getShopAvatarUrl().replaceFirst("http://[^/]+\\.r5\\.cpolar\\.top", ""));
            cartItem.setShopAvatarUrl(cartItem.getShopAvatarUrl().replaceFirst("http://[^/]+\\.r5\\.cpolar\\.top", "")); */
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
}