package com.dailydiscover.service;

import com.dailydiscover.model.ProductCategory;
import java.util.List;

public interface ProductCategoryService {
    
    /**
     * 根据父分类ID查询子分类
     */
    List<ProductCategory> findByParentId(Long parentId);
    
    /**
     * 查询所有启用状态的分类
     */
    List<ProductCategory> findAllActiveCategories();
    
    /**
     * 根据分类级别查询分类
     */
    List<ProductCategory> findByLevel(Integer level);
    
    /**
     * 根据分类名称模糊查询
     */
    List<ProductCategory> findByNameLike(String name);
    
    /**
     * 查询根分类
     */
    List<ProductCategory> findRootCategories();
}