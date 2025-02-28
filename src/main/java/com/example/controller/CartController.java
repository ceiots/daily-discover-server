package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.dto.UpdateCartItemRequest;
import com.example.model.CartItem;
import com.example.service.CartService;
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;


    @GetMapping("/{userId}")
    public List<CartItem> getCartItems(@PathVariable Long userId) {
       
        List<CartItem> cartItems = cartService.getCartItems(userId);
        return cartItems;
    }

    @PostMapping("/add")
    public void addCartItem(@RequestBody CartItem cartItem) {
        System.out.println("Adding cart item: " + cartItem);
        cartService.addCartItem(cartItem);
    }

    @PutMapping("/update/{itemId}")
    public void updateCartItemQuantity(@PathVariable Long itemId, @RequestBody UpdateCartItemRequest request) {
        cartService.updateCartItemQuantity(itemId, request.getQuantity());
    }

    @DeleteMapping("/delete/{itemId}")
    public void deleteCartItem(@PathVariable Long itemId) {
        cartService.deleteCartItem(itemId);
    }
}