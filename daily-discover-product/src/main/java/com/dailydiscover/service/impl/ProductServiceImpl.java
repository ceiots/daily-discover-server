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
    public List<Product> findRecommendedProducts() {
        return productMapper.findRecommendedProducts();
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
            if (result.getSeller_id() == null) result.setSeller_id(0L);
            if (result.getCategory_id() == null) result.setCategory_id(0L);
            if (result.getMin_price() == null) result.setMin_price(new BigDecimal("0.00"));
            if (result.getMax_price() == null) result.setMax_price(new BigDecimal("0.00"));
            if (result.getMain_image_url() == null) result.setMain_image_url("");
            if (result.getDiscount() == null) result.setDiscount(new BigDecimal("0.00"));
            if (result.getSales_count() == null) result.setSales_count(0);
            if (result.getUrgency_hint() == null) result.setUrgency_hint("");
            if (result.getAverage_rating() == null) result.setAverage_rating(new BigDecimal("0.00"));
            if (result.getTotal_reviews() == null) result.setTotal_reviews(0);
            if (result.getTitle() == null) result.setTitle("");
            if (result.getSeller_name() == null) result.setSeller_name("");
            if (result.getSeller_rating() == null) result.setSeller_rating("");
            if (result.getRecommendationTitle() == null) result.setRecommendationTitle("");
            if (result.getRecommendationDescription() == null) result.setRecommendationDescription("");
            if (result.getRecommendationMetadata() == null) result.setRecommendationMetadata("");
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