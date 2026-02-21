package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductDetailMapper;
import com.dailydiscover.model.ProductDetail;
import com.dailydiscover.service.ProductDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品详情服务实现类（电商媒体管理）
 */
@Service
@Slf4j
public class ProductDetailServiceImpl extends ServiceImpl<ProductDetailMapper, ProductDetail> implements ProductDetailService {
    
    @Autowired
    private ProductDetailMapper productDetailMapper;
    
    @Override
    public List<ProductDetail> findByProductId(Long productId) {
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
    public boolean save(ProductDetail productDetail) {
        return super.save(productDetail);
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
    public List<ProductDetail> findByProductIdAndMediaType(Long productId, Integer mediaType) {
        return productDetailMapper.findByProductIdAndMediaType(productId, mediaType);
    }
    
    @Override
    public List<ProductDetail> getProductCarousel(Long productId) {
        return productDetailMapper.getProductCarousel(productId);
    }
    
    @Override
    public List<ProductDetail> getProductDetailImages(Long productId) {
        return productDetailMapper.getProductDetailImages(productId);
    }
    
    @Override
    public List<ProductDetail> getProductVideos(Long productId) {
        return productDetailMapper.getProductVideos(productId);
    }
    
    @Override
    public List<ProductDetail> getProductImages(Long productId) {
        return productDetailMapper.getProductImages(productId);
    }
    
    @Override
    public boolean deleteByProductId(Long productId) {
        return productDetailMapper.deleteByProductId(productId) > 0;
    }
    
    @Override
    public boolean deleteByProductIdAndMediaType(Long productId, Integer mediaType) {
        return productDetailMapper.deleteByProductIdAndMediaType(productId, mediaType) > 0;
    }
}