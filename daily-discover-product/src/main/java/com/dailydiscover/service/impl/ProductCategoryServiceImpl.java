package com.dailydiscover.service.impl;

import com.dailydiscover.mapper.ProductCategoryMapper;
import com.dailydiscover.model.ProductCategory;
import com.dailydiscover.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {
    
    @Autowired
    private ProductCategoryMapper productCategoryMapper;
    
    @Override
    public ProductCategory findById(Long id) {
        return productCategoryMapper.findById(id);
    }
    
    @Override
    public List<ProductCategory> findAll() {
        return productCategoryMapper.findAll();
    }
    
    @Override
    public List<ProductCategory> findByParentId(Long parentId) {
        return productCategoryMapper.findByParentId(parentId);
    }
    
    @Override
    public List<ProductCategory> findByLevel(Integer level) {
        return productCategoryMapper.findByLevel(level);
    }
    
    @Override
    public void save(ProductCategory category) {
        productCategoryMapper.insert(category);
    }
    
    @Override
    public void update(ProductCategory category) {
        productCategoryMapper.update(category);
    }
    
    @Override
    public void deactivate(Long id) {
        productCategoryMapper.deactivate(id);
    }
}