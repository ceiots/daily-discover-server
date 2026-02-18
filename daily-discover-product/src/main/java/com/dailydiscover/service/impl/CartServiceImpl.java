package com.dailydiscover.service.impl;

import com.dailydiscover.mapper.ShoppingCartMapper;
import com.dailydiscover.model.ShoppingCart;
import com.dailydiscover.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CartServiceImpl implements CartService {
    
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    
    @Override
    public List<ShoppingCart> findByUserId(Long userId) {
        return shoppingCartMapper.findByUserId(userId);
    }
    
    @Override
    public List<ShoppingCart> findSelectedItemsByUserId(Long userId) {
        return shoppingCartMapper.findSelectedItemsByUserId(userId);
    }
    
    @Override
    public ShoppingCart findByUserIdAndSkuId(Long userId, Long skuId) {
        return shoppingCartMapper.findByUserIdAndSkuId(userId, skuId);
    }
    
    @Override
    public Integer countCartItems(Long userId) {
        return shoppingCartMapper.countCartItems(userId);
    }
}