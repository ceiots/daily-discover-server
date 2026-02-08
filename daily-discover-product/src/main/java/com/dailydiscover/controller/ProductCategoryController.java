package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.ProductCategory;
import com.dailydiscover.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 商品分类控制器 - RESTful风格
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class ProductCategoryController {
    
    private final ProductCategoryService productCategoryService;
    
    @GetMapping("/{id}")
    @ApiLog("获取分类详情")
    public ResponseEntity<ProductCategory> getCategoryById(@PathVariable Long id) {
        try {
            ProductCategory category = productCategoryService.findById(id);
            if (category != null) {
                return ResponseEntity.ok(category);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping
    @ApiLog("获取所有分类")
    public ResponseEntity<List<ProductCategory>> getAllCategories() {
        try {
            List<ProductCategory> categories = productCategoryService.findAll();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/parent/{parentId}")
    @ApiLog("根据父级获取分类")
    public ResponseEntity<List<ProductCategory>> getCategoriesByParent(@PathVariable Long parentId) {
        try {
            List<ProductCategory> categories = productCategoryService.findByParentId(parentId);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/level/{level}")
    @ApiLog("根据层级获取分类")
    public ResponseEntity<List<ProductCategory>> getCategoriesByLevel(@PathVariable Integer level) {
        try {
            List<ProductCategory> categories = productCategoryService.findByLevel(level);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    @ApiLog("创建分类")
    public ResponseEntity<ProductCategory> createCategory(@RequestBody ProductCategory category) {
        try {
            productCategoryService.save(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(category);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    @ApiLog("更新分类")
    public ResponseEntity<ProductCategory> updateCategory(@PathVariable Long id, @RequestBody ProductCategory category) {
        try {
            category.setId(id);
            productCategoryService.update(category);
            return ResponseEntity.ok(category);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    @ApiLog("禁用分类")
    public ResponseEntity<Void> deactivateCategory(@PathVariable Long id) {
        try {
            productCategoryService.deactivate(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}