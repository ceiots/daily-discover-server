package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductMapper;
import com.dailydiscover.model.Product;
import com.dailydiscover.model.dto.ProductBasicInfoDTO;
import com.dailydiscover.service.ProductService;
import com.dailydiscover.common.logging.LogTracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    public Product findById(Long id) {
        return getById(id);
    }
    
    @Override
    public ProductBasicInfoDTO findBasicInfoById(Long id) {
        // 追踪业务方法调用
        LogTracer.traceBusinessMethodWithParams(id);
        
        ProductBasicInfoDTO result = productMapper.findBasicInfoById(id);
        
        // 确保所有字段都有默认值，避免返回null
        if (result != null) {
            // 设置默认值
            if (result.getSellerId() == null) result.setSellerId(0L);
            if (result.getCategoryId() == null) result.setCategoryId(0L);
            if (result.getMinPrice() == null) result.setMinPrice(new BigDecimal("0.00"));
            if (result.getMaxPrice() == null) result.setMaxPrice(new BigDecimal("0.00"));
            if (result.getMainImageUrl() == null) result.setMainImageUrl("");
            if (result.getDiscount() == null) result.setDiscount(new BigDecimal("0.00"));
            if (result.getSalesCount() == null) result.setSalesCount(0);
            if (result.getUrgencyHint() == null) result.setUrgencyHint("");
            if (result.getAverageRating() == null) result.setAverageRating(new BigDecimal("0.00"));
            if (result.getTotalReviews() == null) result.setTotalReviews(0);
            if (result.getTitle() == null) result.setTitle("");
            if (result.getSellerName() == null) result.setSellerName("");
            if (result.getSellerRating() == null) result.setSellerRating("");
        }
        
        // 追踪数据库查询结果
        LogTracer.traceDatabaseQuery("findBasicInfoById", id, result);
        
        // 追踪业务方法结果
        LogTracer.traceBusinessMethodWithResult(result);
        
        return result;
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