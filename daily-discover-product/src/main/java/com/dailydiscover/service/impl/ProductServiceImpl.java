package com.dailydiscover.service.impl;

import com.dailydiscover.mapper.ProductMapper;
import com.dailydiscover.model.Product;
import com.dailydiscover.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    public Product findById(Long id) {
        return productMapper.findById(id);
    }
    
    @Override
    public List<Product> findAll() {
        return productMapper.findAll();
    }
    
    @Override
    public List<Product> findBySellerId(Long sellerId) {
        return productMapper.findBySellerId(sellerId);
    }
    
    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        return productMapper.findByCategoryId(categoryId);
    }
    
    @Override
    public List<Product> findHotProducts() {
        return productMapper.findHotProducts();
    }
    
    @Override
    public List<Product> findNewProducts() {
        return productMapper.findNewProducts();
    }
    
    @Override
    public List<Product> findRecommendedProducts() {
        return productMapper.findRecommendedProducts();
    }
    
    @Override
    public void save(Product product) {
        productMapper.insert(product);
    }
    
    @Override
    public void update(Product product) {
        productMapper.update(product);
    }
    
    @Override
    public void delete(Long id) {
        productMapper.softDelete(id);
    }
}