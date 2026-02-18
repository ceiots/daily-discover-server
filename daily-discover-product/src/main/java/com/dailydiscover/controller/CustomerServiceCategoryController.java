package com.dailydiscover.controller;

import com.dailydiscover.common.annotation.ApiLog;
import com.dailydiscover.model.CustomerServiceCategory;
import com.dailydiscover.service.CustomerServiceCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer-service-categories")
@RequiredArgsConstructor
public class CustomerServiceCategoryController {

    private final CustomerServiceCategoryService customerServiceCategoryService;

    @GetMapping
    @ApiLog("获取所有客服分类")
    public ResponseEntity<List<CustomerServiceCategory>> getAllCustomerServiceCategories() {
        try {
            List<CustomerServiceCategory> categories = customerServiceCategoryService.list();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @ApiLog("根据ID获取客服分类")
    public ResponseEntity<CustomerServiceCategory> getCustomerServiceCategoryById(@PathVariable Long id) {
        try {
            CustomerServiceCategory category = customerServiceCategoryService.getById(id);
            return category != null ? ResponseEntity.ok(category) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/active")
    @ApiLog("获取启用的客服分类")
    public ResponseEntity<List<CustomerServiceCategory>> getActiveCustomerServiceCategories() {
        try {
            List<CustomerServiceCategory> categories = customerServiceCategoryService.findActiveCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/parent/{parentId}")
    @ApiLog("根据父级分类ID获取子分类")
    public ResponseEntity<List<CustomerServiceCategory>> getCustomerServiceCategoriesByParentId(@PathVariable Long parentId) {
        try {
            List<CustomerServiceCategory> categories = customerServiceCategoryService.findByParentId(parentId);
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/top-level")
    @ApiLog("获取顶级客服分类")
    public ResponseEntity<List<CustomerServiceCategory>> getTopLevelCustomerServiceCategories() {
        try {
            List<CustomerServiceCategory> categories = customerServiceCategoryService.findTopLevelCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/tree")
    @ApiLog("获取客服分类树结构")
    public ResponseEntity<List<CustomerServiceCategory>> getCustomerServiceCategoryTree() {
        try {
            List<CustomerServiceCategory> categories = customerServiceCategoryService.getCategoryTree();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @ApiLog("创建客服分类")
    public ResponseEntity<CustomerServiceCategory> createCustomerServiceCategory(@RequestBody CustomerServiceCategory category) {
        try {
            boolean success = customerServiceCategoryService.save(category);
            return success ? ResponseEntity.ok(category) : ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @ApiLog("更新客服分类")
    public ResponseEntity<CustomerServiceCategory> updateCustomerServiceCategory(@PathVariable Long id, @RequestBody CustomerServiceCategory category) {
        try {
            category.setId(id);
            boolean success = customerServiceCategoryService.updateById(category);
            return success ? ResponseEntity.ok(category) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/status")
    @ApiLog("更新客服分类状态")
    public ResponseEntity<Void> updateCustomerServiceCategoryStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            boolean success = customerServiceCategoryService.updateCategoryStatus(id, status);
            return success ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @ApiLog("删除客服分类")
    public ResponseEntity<Void> deleteCustomerServiceCategory(@PathVariable Long id) {
        try {
            boolean success = customerServiceCategoryService.removeById(id);
            return success ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}