package com.dailydiscover.service;

import com.dailydiscover.model.CustomerServiceCategory;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 客服分类服务接口
 */
public interface CustomerServiceCategoryService extends IService<CustomerServiceCategory> {
    
    /**
     * 查询所有启用的客服分类
     */
    java.util.List<CustomerServiceCategory> findActiveCategories();
    
    /**
     * 根据父级分类ID查询子分类
     */
    java.util.List<CustomerServiceCategory> findByParentId(Long parentId);
    
    /**
     * 查询顶级分类
     */
    java.util.List<CustomerServiceCategory> findTopLevelCategories();
    
    /**
     * 根据分类名称模糊查询
     */
    java.util.List<CustomerServiceCategory> findByNameLike(String name);
    
    /**
     * 创建客服分类
     */
    CustomerServiceCategory createCategory(String categoryName, Long parentId, Integer sortOrder);
    
    /**
     * 更新分类状态
     */
    boolean updateCategoryStatus(Long categoryId, String status);
    
    /**
     * 更新分类排序
     */
    boolean updateCategorySortOrder(Long categoryId, Integer sortOrder);
    
    /**
     * 获取分类树结构
     */
    java.util.List<CustomerServiceCategory> getCategoryTree();
}