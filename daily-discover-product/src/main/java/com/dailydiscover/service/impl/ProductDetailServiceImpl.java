package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductDetailMapper;
import com.dailydiscover.model.ProductDetail;
import com.dailydiscover.service.ProductDetailService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductDetailServiceImpl extends ServiceImpl<ProductDetailMapper, ProductDetail> implements ProductDetailService {
    
    @Autowired
    private ProductDetailMapper productDetailMapper;
    
    @Override
    public ProductDetail findByProductId(Long productId) {
        return productDetailMapper.findByProductId(productId);
    }
    
    @Override
    public ProductDetail findById(Long id) {
        return getById(id);
    }
    
    @Override
    public List<ProductDetail> findAll() {
        return list();
    }
    
    @Override
    public ProductDetail save(ProductDetail productDetail) {
        super.save(productDetail);
        return productDetail;
    }
    
    @Override
    public ProductDetail update(ProductDetail productDetail) {
        updateById(productDetail);
        return productDetail;
    }
    
    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }
    
    @Override
    public java.util.List<String> getProductImages(Long productId) {
        ProductDetail detail = findByProductId(productId);
        if (detail != null && detail.getMediaUrl() != null) {
            return java.util.List.of(detail.getMediaUrl().split(","));
        }
        return java.util.Collections.emptyList();
    }
    
    @Override
    public java.util.List<String> getProductSpecifications(Long productId) {
        ProductDetail detail = findByProductId(productId);
        if (detail != null && detail.getSpecifications() != null) {
            return java.util.List.of(detail.getSpecifications().split(","));
        }
        return java.util.Collections.emptyList();
    }
    
    @Override
    public java.util.List<String> getProductFeatures(Long productId) {
        ProductDetail detail = findByProductId(productId);
        if (detail != null && detail.getFeatures() != null) {
            return java.util.List.of(detail.getFeatures().split(","));
        }
        return java.util.Collections.emptyList();
    }
}