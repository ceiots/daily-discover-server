package com.example.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mapper.ProductContentMapper;
import com.example.model.ProductContent;
import com.example.service.ProductContentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * 商品内容服务实现类
 */
@Slf4j
@Service
public class ProductContentServiceImpl implements ProductContentService {

    @Autowired
    private ProductContentMapper productContentMapper;
    
    @Override
    public ProductContent getByProductId(Long productId) {
        if (productId == null) {
            return null;
        }
        
        try {
            return productContentMapper.findByProductId(productId);
        } catch (Exception e) {
            log.error("获取商品内容失败, productId: {}", productId, e);
            return null;
        }
    }
    
    @Override
    @Transactional
    public ProductContent create(ProductContent content) {
        if (content == null || content.getProductId() == null) {
            throw new IllegalArgumentException("商品内容参数无效");
        }
        
        try {
            // 检查是否已存在
            ProductContent existingContent = getByProductId(content.getProductId());
            if (existingContent != null) {
                content.setId(existingContent.getId());
                if (update(content)) {
                    return content;
                }
                return existingContent;
            }
            
            productContentMapper.insert(content);
            return content;
        } catch (Exception e) {
            log.error("创建商品内容失败", e);
            throw new RuntimeException("创建商品内容失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public boolean update(ProductContent content) {
        if (content == null || content.getProductId() == null) {
            return false;
        }
        
        try {
            return productContentMapper.updateByProductId(content) > 0;
        } catch (Exception e) {
            log.error("更新商品内容失败, productId: {}", content.getProductId(), e);
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
            return productContentMapper.deleteByProductId(productId) > 0;
        } catch (Exception e) {
            log.error("删除商品内容失败, productId: {}", productId, e);
            return false;
        }
    }
    
    @Override
    public List<ProductContent> getByProductIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        try {
            List<ProductContent> result = new ArrayList<>();
            for (Long productId : productIds) {
                ProductContent content = getByProductId(productId);
                if (content != null) {
                    result.add(content);
                }
            }
            return result;
        } catch (Exception e) {
            log.error("批量获取商品内容失败", e);
            return new ArrayList<>();
        }
    }
} 