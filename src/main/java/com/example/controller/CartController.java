package com.example.controller;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;

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
import com.example.common.api.CommonResult;
import com.example.dto.ShopCartGroupDTO;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;


    @GetMapping("/{userId}")
    public CommonResult<List<ShopCartGroupDTO>> getCartItems(@PathVariable Long userId) {
        try {
            List<CartItem> cartItems = cartService.getCartItems(userId);
            // 按shopId分组
            Map<Long, ShopCartGroupDTO> groupMap = new LinkedHashMap<>();
            for (CartItem item : cartItems) {
                ShopCartGroupDTO group = groupMap.get(item.getShopId());
                if (group == null) {
                    group = new ShopCartGroupDTO();
                    group.setShopId(item.getShopId());
                    group.setShopName(item.getShopName());
                    group.setShopAvatarUrl(item.getShopAvatarUrl());
                    group.setItems(new ArrayList<>());
                    groupMap.put(item.getShopId(), group);
                }
                group.getItems().add(item);
            }
            return CommonResult.success(new ArrayList<>(groupMap.values()));
        } catch (Exception e) {
            return CommonResult.failed("获取购物车失败: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    public CommonResult<?> addCartItem(@RequestBody CartItem cartItem) {
        try {
            return cartService.addCartItem(cartItem);
        } catch (Exception e) {
            return CommonResult.failed("加入购物车失败: " + e.getMessage());
        }
    }

    @PutMapping("/update/{itemId}")
    public CommonResult<?> updateCartItemQuantity(@PathVariable Long itemId, @RequestBody UpdateCartItemRequest request) {
        try {
            cartService.updateCartItemQuantity(itemId, request.getQuantity());
            return CommonResult.success(null);
        } catch (Exception e) {
            return CommonResult.failed("更新购物车商品数量失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{itemId}")
    public CommonResult<?> deleteCartItem(@PathVariable Long itemId) {
        try {
            cartService.deleteCartItem(itemId);
            return CommonResult.success(null);
        } catch (Exception e) {
            return CommonResult.failed("删除购物车商品失败: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}/count")
    public CommonResult<Integer> getCartItemCount(@PathVariable("userId") Long userId) {
        try {
            int count = cartService.getCartItemCount(userId);
            return CommonResult.success(count);
        } catch (Exception e) {
            return CommonResult.failed("获取购物车商品数量失败: " + e.getMessage());
        }
    }
}