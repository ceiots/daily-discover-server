package com.example.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mapper.ProductSpecificationMapper;
import com.example.model.ProductSpecification;
import com.example.service.ProductSpecificationService;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * 商品规格服务实现类
 */
@Slf4j
@Service
public class ProductSpecificationServiceImpl implements ProductSpecificationService {

    @Autowired
    private ProductSpecificationMapper productSpecificationMapper;
    
    @Override
    public ProductSpecification getByProductId(Long productId) {
        if (productId == null) {
            return null;
        }
        
        try {
            return productSpecificationMapper.findByProductId(productId);
        } catch (Exception e) {
            log.error("获取商品规格失败, productId: {}", productId, e);
            return null;
        }
    }
    
    @Override
    @Transactional
    public ProductSpecification create(ProductSpecification specification) {
        if (specification == null || specification.getProductId() == null) {
            throw new IllegalArgumentException("商品规格参数无效");
        }
        
        try {
            // 检查是否已存在
            ProductSpecification existingSpec = getByProductId(specification.getProductId());
            if (existingSpec != null) {
                specification.setId(existingSpec.getId());
                if (update(specification)) {
                    return specification;
                }
                return existingSpec;
            }
            
            productSpecificationMapper.insert(specification);
            return specification;
        } catch (Exception e) {
            log.error("创建商品规格失败", e);
            throw new RuntimeException("创建商品规格失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public boolean update(ProductSpecification specification) {
        if (specification == null || specification.getProductId() == null) {
            return false;
        }
        
        try {
            return productSpecificationMapper.updateByProductId(specification) > 0;
        } catch (Exception e) {
            log.error("更新商品规格失败, productId: {}", specification.getProductId(), e);
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
            return productSpecificationMapper.deleteByProductId(productId) > 0;
        } catch (Exception e) {
            log.error("删除商品规格失败, productId: {}", productId, e);
            return false;
        }
    }
    
    @Override
    public List<ProductSpecification> getByProductIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            List<ProductSpecification> result = new ArrayList<>();
            for (Long productId : productIds) {
                ProductSpecification spec = getByProductId(productId);
                if (spec != null) {
                    result.add(spec);
                }
            }
            return result;
        } catch (Exception e) {
            log.error("批量获取商品规格失败", e);
            return new ArrayList<>();
        }
    }
} 