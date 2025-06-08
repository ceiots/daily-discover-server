package com.example.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mapper.ProductPromotionMapper;
import com.example.model.ProductPromotion;
import com.example.service.ProductPromotionService;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * 商品促销服务实现类
 */
@Slf4j
@Service
public class ProductPromotionServiceImpl implements ProductPromotionService {

    @Autowired
    private ProductPromotionMapper productPromotionMapper;
    
    @Override
    public ProductPromotion getById(Long id) {
        if (id == null) {
            return null;
        }
        
        try {
            return productPromotionMapper.findById(id);
        } catch (Exception e) {
            log.error("获取促销信息失败, id: {}", id, e);
            return null;
        }
    }
    
    @Override
    public List<ProductPromotion> getByProductId(Long productId) {
        if (productId == null) {
            return new ArrayList<>();
        }
        
        try {
            return productPromotionMapper.findByProductId(productId);
        } catch (Exception e) {
            log.error("获取商品促销信息失败, productId: {}", productId, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<ProductPromotion> getBySkuId(Long skuId) {
        if (skuId == null) {
            return new ArrayList<>();
        }
        
        try {
            return productPromotionMapper.findBySkuId(skuId);
        } catch (Exception e) {
            log.error("获取SKU促销信息失败, skuId: {}", skuId, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<ProductPromotion> getActivePromotions(Long productId, Long skuId) {
        if (productId == null) {
            return new ArrayList<>();
        }
        
        try {
            return productPromotionMapper.findActivePromotions(productId, skuId);
        } catch (Exception e) {
            log.error("获取有效促销信息失败, productId: {}, skuId: {}", productId, skuId, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    @Transactional
    public ProductPromotion create(ProductPromotion promotion) {
        if (promotion == null || promotion.getProductId() == null) {
            throw new IllegalArgumentException("促销信息参数无效");
        }
        
        try {
            productPromotionMapper.insert(promotion);
            return promotion;
        } catch (Exception e) {
            log.error("创建促销信息失败", e);
            throw new RuntimeException("创建促销信息失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public boolean update(ProductPromotion promotion) {
        if (promotion == null || promotion.getId() == null) {
            return false;
        }
        
        try {
            return productPromotionMapper.update(promotion) > 0;
        } catch (Exception e) {
            log.error("更新促销信息失败, id: {}", promotion.getId(), e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean delete(Long id) {
        if (id == null) {
            return false;
        }
        
        try {
            return productPromotionMapper.deleteById(id) > 0;
        } catch (Exception e) {
            log.error("删除促销信息失败, id: {}", id, e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean updateStatus(Long id, Integer status) {
        if (id == null || status == null) {
            return false;
        }
        
        try {
            return productPromotionMapper.updateStatus(id, status) > 0;
        } catch (Exception e) {
            log.error("更新促销状态失败, id: {}, status: {}", id, status, e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean deleteByProductId(Long productId) {
        if (productId == null) {
            return false;
        }
        
        try {
            return productPromotionMapper.deleteByProductId(productId) > 0;
        } catch (Exception e) {
            log.error("删除商品促销信息失败, productId: {}", productId, e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean deleteBySkuId(Long skuId) {
        if (skuId == null) {
            return false;
        }
        
        try {
            return productPromotionMapper.deleteBySkuId(skuId) > 0;
        } catch (Exception e) {
            log.error("删除SKU促销信息失败, skuId: {}", skuId, e);
            return false;
        }
    }
} 