package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductCategoryMapper;
import com.dailydiscover.model.ProductCategory;
import com.dailydiscover.service.ProductCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductCategoryServiceImpl extends ServiceImpl<ProductCategoryMapper, ProductCategory> implements ProductCategoryService {
    
    @Autowired
    private ProductCategoryMapper productCategoryMapper;
    
    @Override
    public List<ProductCategory> findByParentId(Long parentId) {
        return productCategoryMapper.findByParentId(parentId);
    }
    
    @Override
    public List<ProductCategory> findAllActiveCategories() {
        return productCategoryMapper.findAllActiveCategories();
    }
    
    @Override
    public List<ProductCategory> findByLevel(Integer level) {
        return productCategoryMapper.findByLevel(level);
    }
    
    @Override
    public List<ProductCategory> findByNameLike(String name) {
        return productCategoryMapper.findByNameLike(name);
    }
    
    @Override
    public List<ProductCategory> findRootCategories() {
        return productCategoryMapper.findRootCategories();
    }
    
    @Override
    public List<ProductCategory> findAll() {
        return list();
    }
    
    @Override
    public ProductCategory findById(Long id) {
        return getById(id);
    }
    
    @Override
    public boolean deactivate(Long id) {
        ProductCategory category = getById(id);
        if (category != null) {
            category.setStatus(0); // 0表示停用，1表示启用
            return updateById(category);
        }
        return false;
    }
}