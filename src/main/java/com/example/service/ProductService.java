package com.example.service;

import com.example.mapper.ProductMapper;
import com.example.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductMapper productMapper;

    public List<Product> getAllProducts() {
        return productMapper.getAllProducts();
    }

    public Product getProductById(Long id) {
        return productMapper.findById(id);
    }

    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productMapper.findByCategoryId(categoryId);
    }

    public List<Product> getRandomProducts() {
        return productMapper.findRandom();
    }

    public List<Product> searchProducts(String keyword) {
        return productMapper.searchProducts(keyword);
    }
} 