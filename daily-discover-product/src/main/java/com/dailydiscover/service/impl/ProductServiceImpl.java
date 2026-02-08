package com.dailydiscover.service.impl;

import com.dailydiscover.mapper.ProductMapper;
import com.dailydiscover.mapper.ProductAttributeMapper;
import com.dailydiscover.model.Product;
import com.dailydiscover.model.ProductAttribute;
import com.dailydiscover.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private ProductAttributeMapper productAttributeMapper;
    
    @Override
    public Product findById(Long id) {
        Product product = productMapper.findById(id);
        if (product != null) {
            ProductAttribute attribute = productAttributeMapper.findByProductId(id);
            // 这里可以设置product的属性，但由于表已拆分，需要修改Product模型
            // 或者创建新的组合对象，这里暂时保持原样
        }
        return product;
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