package com.example.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.common.exception.BusinessException;
import com.example.mapper.ProductTagMapper;
import com.example.model.ProductTag;
import com.example.service.ProductTagService;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ProductTagServiceImpl implements ProductTagService {

    @Autowired
    private ProductTagMapper tagMapper;
    
    @Override
    @Transactional
    public ProductTag addTag(Long productId, Long tagId, String tagName, Integer tagType) {
        try {
            // 检查是否已经存在该标签
            ProductTag existingTag = tagMapper.findByProductIdAndTagId(productId, tagId);
            if (existingTag != null) {
                return existingTag;
            }
            
            // 创建新标签关联
            ProductTag tag = ProductTag.create(productId, tagId, tagName, tagType);
            tagMapper.insert(tag);
            return tagMapper.findById(tag.getId());
        } catch (Exception e) {
            log.error("添加商品标签失败", e);
            throw new ApiException("添加商品标签失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public List<ProductTag> batchAddTags(Long productId, List<ProductTag> tags) {
        if (tags == null || tags.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<ProductTag> result = new ArrayList<>();
        for (ProductTag tag : tags) {
            try {
                // 设置商品ID
                tag.setProductId(productId);
                
                // 检查是否已经存在该标签
                ProductTag existingTag = tagMapper.findByProductIdAndTagId(productId, tag.getTagId());
                if (existingTag != null) {
                    result.add(existingTag);
                    continue;
                }
                
                // 创建新标签关联
                tagMapper.insert(tag);
                result.add(tagMapper.findById(tag.getId()));
            } catch (Exception e) {
                log.error("批量添加商品标签失败", e);
                // 继续处理下一个标签
            }
        }
        return result;
    }
    
    @Override
    public List<ProductTag> getProductTags(Long productId) {
        return tagMapper.findByProductId(productId);
    }
    
    @Override
    public List<ProductTag> getTagProducts(Long tagId) {
        return tagMapper.findByTagId(tagId);
    }
    
    @Override
    public List<ProductTag> getTagsByType(Integer tagType) {
        return tagMapper.findByTagType(tagType);
    }
    
    @Override
    public boolean hasTag(Long productId, Long tagId) {
        ProductTag tag = tagMapper.findByProductIdAndTagId(productId, tagId);
        return tag != null;
    }
    
    @Override
    @Transactional
    public boolean removeTag(Long productId, Long tagId) {
        try {
            int rows = tagMapper.deleteByProductIdAndTagId(productId, tagId);
            return rows > 0;
        } catch (Exception e) {
            log.error("移除商品标签失败", e);
            throw new BusinessException("移除商品标签失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean removeAllTags(Long productId) {
        try {
            int rows = tagMapper.deleteByProductId(productId);
            return rows > 0;
        } catch (Exception e) {
            log.error("移除商品所有标签失败", e);
            throw new ApiException("移除商品所有标签失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Long> getProductIdsByTag(Long tagId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 20; // 默认限制20个结果
        }
        return tagMapper.findProductIdsByTagId(tagId, limit);
    }
    
    @Override
    public int countTagUsage(Long tagId) {
        return tagMapper.countByTagId(tagId);
    }
} 