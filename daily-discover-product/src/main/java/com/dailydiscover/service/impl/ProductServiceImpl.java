package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductMapper;
import com.dailydiscover.model.Product;
import com.dailydiscover.model.dto.ProductBasicInfoDTO;
import com.dailydiscover.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    public List<Product> findAllActive() {
        return productMapper.findAllActive();
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
    public Product findById(Long id) {
        return getById(id);
    }
    
    @Override
    public ProductBasicInfoDTO findBasicInfoById(Long id) {
        return productMapper.findBasicInfoById(id);
    }
    
    @Override
    public boolean save(Product product) {
        return save(product);
    }
    
    @Override
    public boolean update(Product product) {
        return updateById(product);
    }
    
    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }
}