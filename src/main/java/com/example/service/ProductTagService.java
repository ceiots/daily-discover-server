package com.example.service;

import org.springframework.transaction.annotation.Transactional;

import com.example.model.ProductTag;

import java.util.List;

public interface ProductTagService {

    /**
     * 添加商品标签
     */
    @Transactional
    ProductTag addTag(Long productId, Long tagId, String tagName, Integer tagType);
    
    /**
     * 批量添加商品标签
     */
    @Transactional
    List<ProductTag> batchAddTags(Long productId, List<ProductTag> tags);
    
    /**
     * 获取商品的所有标签
     */
    List<ProductTag> getProductTags(Long productId);
    
    /**
     * 获取标签下的所有商品
     */
    List<ProductTag> getTagProducts(Long tagId);
    
    /**
     * 获取特定类型的所有标签
     */
    List<ProductTag> getTagsByType(Integer tagType);
    
    /**
     * 检查商品是否有特定标签
     */
    boolean hasTag(Long productId, Long tagId);
    
    /**
     * 移除商品标签
     */
    @Transactional
    boolean removeTag(Long productId, Long tagId);
    
    /**
     * 移除商品的所有标签
     */
    @Transactional
    boolean removeAllTags(Long productId);
    
    /**
     * 根据标签获取商品ID列表
     */
    List<Long> getProductIdsByTag(Long tagId, Integer limit);
    
    /**
     * 统计标签使用次数
     */
    int countTagUsage(Long tagId);
} 