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
        // Check if the cart item already exists
        int count = cartItemMapper.countCartItem(cartItem.getUser_id(), cartItem.getProduct_id());

        if (count > 0) {
            // If it exists, update the quantity
            cartItemMapper.updateCartItem(cartItem.getUser_id(), cartItem.getProduct_id(), cartItem.getQuantity(),
                    cartItem.getProduct_variant());
        } else {
            // If it doesn't exist, insert a new cart item
            cartItemMapper.addCartItem(cartItem);
        }
    }

    public void updateCartItemQuantity(Long itemId, Integer quantity) {
        cartItemMapper.updateCartItemQuantity(itemId, quantity);
    }
}