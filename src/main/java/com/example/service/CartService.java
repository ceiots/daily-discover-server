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
        // 添加逻辑，可能包括检查库存等
    }

    public void updateCartItemQuantity(Long itemId, int quantity) {
        // 更新逻辑，可能包括检查库存等
    }

    public void deleteCartItem(Long itemId) {
        // 删除逻辑
    }
}