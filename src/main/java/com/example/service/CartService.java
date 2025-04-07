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

        CartItem existingItem = findExistingCartItem(cartItem);
        System.out.println("Adding cart existingItem: " + cartItem);
        if (existingItem != null) {
            updateExistingCartItem(existingItem, cartItem);
        } else {
            addNewCartItem(cartItem);
        }
    }

    private CartItem findExistingCartItem(CartItem cartItem) {
        return cartItemMapper.findByUserIdAndProductId(cartItem.getUserId(), cartItem.getProductId());
    }

    private void updateExistingCartItem(CartItem existingItem, CartItem newItem) {
        int newQuantity = existingItem.getQuantity() + newItem.getQuantity();
        existingItem.setQuantity(newQuantity);
        cartItemMapper.updateCartItemQuantity(existingItem.getId(), newQuantity);
        logger.info("Updated cart item quantity for item ID: {}", existingItem.getId());
    }

    private void addNewCartItem(CartItem cartItem) {
        cleanImageUrls(cartItem);
        cartItemMapper.addCartItem(cartItem);
        logger.info("Added new cart item with product ID: {}", cartItem.getProductId());
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