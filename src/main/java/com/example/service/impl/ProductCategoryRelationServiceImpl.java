package com.example.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.common.exception.ApiException;
import com.example.mapper.CategoryMapper;
import com.example.mapper.ProductCategoryRelationMapper;
import com.example.model.Category;
import com.example.model.ProductCategoryRelation;
import com.example.service.ProductCategoryRelationService;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ProductCategoryRelationServiceImpl implements ProductCategoryRelationService {

    @Autowired
    private ProductCategoryRelationMapper relationMapper;
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Override
    @Transactional
    public ProductCategoryRelation create(ProductCategoryRelation relation) {
        try {
            // 设置默认值
            if (relation.getIsPrimary() == null) {
                relation.setIsPrimary(false);
            }
            
            if (relation.getSortWeight() == null) {
                relation.setSortWeight(0);
            }
            
            // 设置创建时间
            relation.setCreateTime(new Date());
            
            // 如果只提供了 categoryId，则查询填充父级分类
            if (relation.getCategoryId() != null && 
                (relation.getParentCategoryId() == null || relation.getGrandCategoryId() == null)) {
                
                Category category = categoryMapper.findById(relation.getCategoryId());
                if (category != null) {
                    relation.setParentCategoryId(category.getParentId());
                    
                    if (category.getParentId() != null) {
                        Category parentCategory = categoryMapper.findById(category.getParentId());
                        if (parentCategory != null) {
                            relation.setGrandCategoryId(parentCategory.getParentId());
                        }
                    }
                }
            }
            
            // 如果是主分类，则其他分类设置为非主分类
            if (relation.getIsPrimary()) {
                resetPrimaryCategories(relation.getProductId());
            }
            
            relationMapper.insert(relation);
            return relationMapper.findById(relation.getId());
        } catch (Exception e) {
            log.error("创建商品分类关联失败", e);
            throw new ApiException("创建商品分类关联失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public List<ProductCategoryRelation> batchCreate(List<ProductCategoryRelation> relations) {
        if (relations == null || relations.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<ProductCategoryRelation> result = new ArrayList<>();
        for (ProductCategoryRelation relation : relations) {
            try {
                result.add(create(relation));
            } catch (Exception e) {
                log.error("批量创建商品分类关联失败", e);
                // 继续处理下一个，不中断
            }
        }
        return result;
    }
    
    @Override
    public ProductCategoryRelation getById(Long id) {
        return relationMapper.findById(id);
    }
    
    @Override
    public List<ProductCategoryRelation> getByProductId(Long productId) {
        return relationMapper.findByProductId(productId);
    }
    
    @Override
    public List<ProductCategoryRelation> getByCategoryId(Long categoryId) {
        return relationMapper.findByCategoryId(categoryId);
    }
    
    @Override
    public ProductCategoryRelation getPrimaryByProductId(Long productId) {
        return relationMapper.findPrimaryByProductId(productId);
    }
    
    @Override
    @Transactional
    public boolean update(ProductCategoryRelation relation) {
        try {
            // 如果是主分类，则其他分类设置为非主分类
            if (relation.getIsPrimary()) {
                resetPrimaryCategories(relation.getProductId());
            }
            
            int rows = relationMapper.update(relation);
            return rows > 0;
        } catch (Exception e) {
            log.error("更新商品分类关联失败", e);
            throw new ApiException("更新商品分类关联失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean delete(Long id) {
        try {
            int rows = relationMapper.deleteById(id);
            return rows > 0;
        } catch (Exception e) {
            log.error("删除商品分类关联失败", e);
            throw new ApiException("删除商品分类关联失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean deleteByProductId(Long productId) {
        try {
            int rows = relationMapper.deleteByProductId(productId);
            return rows > 0;
        } catch (Exception e) {
            log.error("删除商品所有分类关联失败", e);
            throw new ApiException("删除商品所有分类关联失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean setPrimaryCategory(Long productId, Long categoryId) {
        try {
            // 首先将所有分类设置为非主分类
            resetPrimaryCategories(productId);
            
            // 找到目标分类关联
            List<ProductCategoryRelation> relations = relationMapper.findByProductId(productId);
            for (ProductCategoryRelation relation : relations) {
                if (relation.getCategoryId().equals(categoryId)) {
                    // 设置为主分类
                    relation.setIsPrimary(true);
                    relationMapper.update(relation);
                    return true;
                }
            }
            
            // 如果没有找到该分类关联，则创建一个
            Category category = categoryMapper.findById(categoryId);
            if (category != null) {
                ProductCategoryRelation relation = new ProductCategoryRelation();
                relation.setProductId(productId);
                relation.setCategoryId(categoryId);
                relation.setParentCategoryId(category.getParentId());
                
                if (category.getParentId() != null) {
                    Category parentCategory = categoryMapper.findById(category.getParentId());
                    if (parentCategory != null) {
                        relation.setGrandCategoryId(parentCategory.getParentId());
                    }
                }
                
                relation.setIsPrimary(true);
                relation.setSortWeight(0);
                relation.setCreateTime(new Date());
                
                relationMapper.insert(relation);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            log.error("设置商品主分类失败", e);
            throw new ApiException("设置商品主分类失败: " + e.getMessage());
        }
    }
    
    @Override
    public int countByCategoryId(Long categoryId) {
        return relationMapper.countByCategoryId(categoryId);
    }
    
    /**
     * 将商品的所有分类关联设置为非主分类
     */
    private void resetPrimaryCategories(Long productId) {
        List<ProductCategoryRelation> relations = relationMapper.findByProductId(productId);
        for (ProductCategoryRelation relation : relations) {
            if (relation.getIsPrimary()) {
                relation.setIsPrimary(false);
                relationMapper.update(relation);
            }
        }
    }
} 