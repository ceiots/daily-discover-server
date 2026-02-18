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
    
    /**
     * 根据ID查询商品
     */
    Product findById(Long id);
    
    /**
     * 查询所有商品
     */
    List<Product> findAll();
    
    /**
     * 保存商品
     */
    Product save(Product product);
    
    /**
     * 更新商品
     */
    Product update(Product product);
    
    /**
     * 删除商品
     */
    boolean delete(Long id);
}