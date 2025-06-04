package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.example.common.api.CommonResult;
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
    public CommonResult<?> addCartItem(@RequestBody CartItem cartItem) {
        try {
            cartService.addCartItem(cartItem);
            return CommonResult.success(null); // 或返回cartItem等
        } catch (Exception e) {
            return CommonResult.failed("加入购物车失败: " + e.getMessage());
        }
    }

    @PutMapping("/update/{itemId}")
    public void updateCartItemQuantity(@PathVariable Long itemId, @RequestBody UpdateCartItemRequest request) {
        cartService.updateCartItemQuantity(itemId, request.getQuantity());
    }

    @DeleteMapping("/delete/{itemId}")
    public void deleteCartItem(@PathVariable Long itemId) {
        cartService.deleteCartItem(itemId);
    }

    @GetMapping("/{userId}/count")
    public ResponseEntity<Integer> getCartItemCount(@PathVariable("userId") Long userId) { // 显式声明路径变量名称
        System.out.println("Getting cart item count for user: " + userId);
        int count = cartService.getCartItemCount(userId);
        System.out.println("Cart item count: " + count);
        return ResponseEntity.ok(count);
    }
}