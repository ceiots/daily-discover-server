package com.dailydiscover.service;

import com.dailydiscover.model.Product;
import com.dailydiscover.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    // 获取所有活跃商品
    public List<Product> getAllActiveProducts() {
        return productMapper.findAllByIsActiveTrue();
    }
    
    // 根据ID获取商品
    public Product getProductById(Long id) {
        return productMapper.findById(id);
    }
    
    // 根据时间段获取推荐商品
    public List<Product> getProductsByTimeSlot(String timeSlot) {
        return productMapper.findByTimeSlotAndIsActiveTrue(timeSlot);
    }
    
    // 根据分类获取商品
    public List<Product> getProductsByCategory(String category) {
        return productMapper.findByCategoryAndIsActiveTrue(category);
    }
    
    // 搜索商品
    public List<Product> searchProducts(String keyword) {
        return productMapper.searchActiveProducts(keyword);
    }
    
    // 创建新商品
    public Product createProduct(Product product) {
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        // 如果没有设置isActive，默认为true
        if (product.getIsActive() == null) {
            product.setIsActive(true);
        }
        productMapper.insert(product);
        return product;
    }
    
    // 更新商品
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        
        product.setTitle(productDetails.getTitle());
        product.setPrice(productDetails.getPrice());
        product.setDescription(productDetails.getDescription());
        product.setImageUrl(productDetails.getImageUrl());
        product.setTimeSlot(productDetails.getTimeSlot());
        product.setCategory(productDetails.getCategory());
        product.setOriginalPrice(productDetails.getOriginalPrice());
        product.setTag(productDetails.getTag());
        product.setReason(productDetails.getReason());
        product.setIsActive(productDetails.getIsActive());
        product.setUpdatedAt(LocalDateTime.now());
        
        productMapper.update(product);
        return product;
    }
    
    // 删除商品（软删除）
    public void deleteProduct(Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        product.setIsActive(false);
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.update(product);
    }
    
    // 获取最新商品
    public List<Product> getLatestProducts() {
        return productMapper.findByIsActiveTrueOrderByCreatedAtDesc();
    }
}