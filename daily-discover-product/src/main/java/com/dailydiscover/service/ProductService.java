package com.dailydiscover.service;

import com.dailydiscover.model.Product;
import java.util.List;

public interface ProductService {
    Product findById(Long id);
    List<Product> findAll();
    List<Product> findBySellerId(Long sellerId);
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findHotProducts();
    List<Product> findNewProducts();
    List<Product> findRecommendedProducts();
    void save(Product product);
    void update(Product product);
    void delete(Long id);
}