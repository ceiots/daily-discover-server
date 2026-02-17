package com.dailydiscover.service.impl;

import com.dailydiscover.dto.ProductDetailDTO;
import com.dailydiscover.mapper.ProductMapper;
import com.dailydiscover.model.Product;
import com.dailydiscover.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    public Product findById(Long id) {
        try {
            Product product = productMapper.findById(id);
            return product;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取产品详情（包含属性信息）
     */
    public ProductDetailDTO getProductDetail(Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            return null;
        }
        
        return new ProductDetailDTO(product, null);
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
    @Transactional
    public void save(Product product) {
        productMapper.insert(product);
        // 由于表已拆分，需要创建ProductAttribute对象并保存
        // 这里需要根据业务逻辑创建ProductAttribute对象
        // 暂时保持原样，后续需要根据实际业务需求调整
    }
    
    @Override
    @Transactional
    public void update(Product product) {
        productMapper.update(product);
        // 由于表已拆分，需要同时更新ProductAttribute表
        // 暂时保持原样，后续需要根据实际业务需求调整
    }
    
    @Override
    public void delete(Long id) {
        productMapper.softDelete(id);
    }
}