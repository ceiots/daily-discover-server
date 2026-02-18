package com.dailydiscover.service;

import com.dailydiscover.model.Product;
import java.util.List;

public interface ProductService {
    
    /**
     * 查询所有启用状态的商品
     */
    List<Product> findAllActive();
    
    /**
     * 根据商家ID查询商品
     */
    List<Product> findBySellerId(Long sellerId);
    
    /**
     * 根据分类ID查询商品
     */
    List<Product> findByCategoryId(Long categoryId);
    
    /**
     * 查询热门商品
     */
    List<Product> findHotProducts();
    
    /**
     * 查询新品
     */
    List<Product> findNewProducts();
    
    /**
     * 查询推荐商品
     */
    List<Product> findRecommendedProducts();
}