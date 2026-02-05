package com.dailydiscover.controller;

import com.dailydiscover.model.ProductCategory;
import com.dailydiscover.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class ProductCategoryController {
    
    @Autowired
    private ProductCategoryService productCategoryService;
    
    @GetMapping("/{id}")
    public ProductCategory getCategoryById(@PathVariable Long id) {
        return productCategoryService.findById(id);
    }
    
    @GetMapping
    public List<ProductCategory> getAllCategories() {
        return productCategoryService.findAll();
    }
    
    @GetMapping("/parent/{parentId}")
    public List<ProductCategory> getCategoriesByParent(@PathVariable Long parentId) {
        return productCategoryService.findByParentId(parentId);
    }
    
    @GetMapping("/level/{level}")
    public List<ProductCategory> getCategoriesByLevel(@PathVariable Integer level) {
        return productCategoryService.findByLevel(level);
    }
    
    @PostMapping
    public void createCategory(@RequestBody ProductCategory category) {
        productCategoryService.save(category);
    }
    
    @PutMapping("/{id}")
    public void updateCategory(@PathVariable Long id, @RequestBody ProductCategory category) {
        category.setId(id);
        productCategoryService.update(category);
    }
    
    @DeleteMapping("/{id}")
    public void deactivateCategory(@PathVariable Long id) {
        productCategoryService.deactivate(id);
    }
}