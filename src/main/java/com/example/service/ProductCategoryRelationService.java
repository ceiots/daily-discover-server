package com.example.service;

import org.springframework.transaction.annotation.Transactional;

import com.example.model.ProductCategoryRelation;

import java.util.List;

public interface ProductCategoryRelationService {

    /**
     * 创建商品分类关联
     */
    @Transactional
    ProductCategoryRelation create(ProductCategoryRelation relation);
    
    /**
     * 批量创建商品分类关联
     */
    @Transactional
    List<ProductCategoryRelation> batchCreate(List<ProductCategoryRelation> relations);
    
    /**
     * 根据ID获取商品分类关联
     */
    ProductCategoryRelation getById(Long id);
    
    /**
     * 根据商品ID获取分类关联列表
     */
    List<ProductCategoryRelation> getByProductId(Long productId);
    
    /**
     * 根据分类ID获取关联商品
     */
    List<ProductCategoryRelation> getByCategoryId(Long categoryId);
    
    /**
     * 获取商品的主分类关联
     */
    ProductCategoryRelation getPrimaryByProductId(Long productId);
    
    /**
     * 更新分类关联
     */
    @Transactional
    boolean update(ProductCategoryRelation relation);
    
    /**
     * 删除分类关联
     */
    @Transactional
    boolean delete(Long id);
    
    /**
     * 删除商品的所有分类关联
     */
    @Transactional
    boolean deleteByProductId(Long productId);
    
    /**
     * 设置主分类（将一个分类设为主分类，其他分类设为非主分类）
     */
    @Transactional
    boolean setPrimaryCategory(Long productId, Long categoryId);
    
    /**
     * 统计分类下的商品数量
     */
    int countByCategoryId(Long categoryId);
} 