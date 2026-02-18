package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.CustomerServiceCategoryMapper;
import com.dailydiscover.model.CustomerServiceCategory;
import com.dailydiscover.service.CustomerServiceCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CustomerServiceCategoryServiceImpl extends ServiceImpl<CustomerServiceCategoryMapper, CustomerServiceCategory> implements CustomerServiceCategoryService {
    
    @Autowired
    private CustomerServiceCategoryMapper customerServiceCategoryMapper;
    
    @Override
    public List<CustomerServiceCategory> findActiveCategories() {
        return customerServiceCategoryMapper.findActiveCategories();
    }
    
    @Override
    public List<CustomerServiceCategory> findByParentId(Long parentId) {
        return customerServiceCategoryMapper.findByParentId(parentId);
    }
    
    @Override
    public List<CustomerServiceCategory> findTopLevelCategories() {
        return customerServiceCategoryMapper.findTopLevelCategories();
    }
    
    @Override
    public List<CustomerServiceCategory> findByNameLike(String name) {
        return customerServiceCategoryMapper.findByNameLike(name);
    }
    
    @Override
    public CustomerServiceCategory createCategory(String categoryName, Long parentId, Integer sortOrder) {
        CustomerServiceCategory category = new CustomerServiceCategory();
        category.setCategoryName(categoryName);
        category.setParentId(parentId);
        category.setSortOrder(sortOrder);
        category.setStatus("active");
        
        save(category);
        return category;
    }
    
    @Override
    public boolean updateCategoryStatus(Long categoryId, String status) {
        CustomerServiceCategory category = getById(categoryId);
        if (category != null) {
            category.setStatus(status);
            return updateById(category);
        }
        return false;
    }
    
    @Override
    public boolean updateCategorySortOrder(Long categoryId, Integer sortOrder) {
        CustomerServiceCategory category = getById(categoryId);
        if (category != null) {
            category.setSortOrder(sortOrder);
            return updateById(category);
        }
        return false;
    }
    
    @Override
    public List<CustomerServiceCategory> getCategoryTree() {
        // 返回顶级分类，子分类可以通过前端递归查询
        return findTopLevelCategories();
    }
}