package com.dailydiscover.service;

import com.dailydiscover.model.ProductCategory;
import java.util.List;

public interface ProductCategoryService {
    ProductCategory findById(Long id);
    List<ProductCategory> findAll();
    List<ProductCategory> findByParentId(Long parentId);
    List<ProductCategory> findByLevel(Integer level);
    void save(ProductCategory category);
    void update(ProductCategory category);
    void deactivate(Long id);
}