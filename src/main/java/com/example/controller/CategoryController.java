package com.example.controller;

import com.example.common.api.CommonResult;
import com.example.dto.CategoryAddRequest;
import com.example.dto.CategoryQueryRequest;
import com.example.model.Category;
import com.example.model.User;
import com.example.service.CategoryService;
import com.example.service.UserService;
import com.example.util.UserIdExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 商品分类接口
 */
@RestController
@RequestMapping("/categories")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据ID获取商品分类
     *
     * @param id 分类ID
     * @return 通用结果，包含分类信息
     */
    @GetMapping("/{id}")
    public CommonResult<Category> getCategoryById(@PathVariable("id") Long id) {
        if (id <= 0) {
            return CommonResult.failed("无效的分类ID");
        }
        
        try {
            Category category = categoryService.getById(id);
            if (category == null) {
                return CommonResult.failed("分类不存在");
            }
            return CommonResult.success(category);
        } catch (Exception e) {
            log.error("获取分类失败", e);
            return CommonResult.failed("获取分类失败：" + e.getMessage());
        }
    }

    @GetMapping
    public CommonResult<List<Category>> listCategories(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "parent_id", required = false) Long parentId,
            @RequestParam(value = "level", required = false) Integer level,
            @RequestParam(value = "status", required = false) Integer status) {
        try {
            CategoryQueryRequest categoryQueryRequest = new CategoryQueryRequest();
            categoryQueryRequest.setName(name);
            categoryQueryRequest.setParentId(parentId);
            categoryQueryRequest.setLevel(level);
            categoryQueryRequest.setStatus(status);
            List<Category> categoryList = categoryService.listCategories(categoryQueryRequest);
            return CommonResult.success(categoryList);
        } catch (Exception e) {
            log.error("获取分类列表失败", e);
            return CommonResult.failed("获取分类列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取商品分类树（带层级结构）
     *
     * @return 通用结果，包含分类树
     */
    @GetMapping("/tree")
    public CommonResult<List<Category>> getCategoryTree() {
        try {
            List<Category> categoryTree = categoryService.getCategoryTree();
            return CommonResult.success(categoryTree);
        } catch (Exception e) {
            log.error("获取分类树失败", e);
            return CommonResult.failed("获取分类树失败：" + e.getMessage());
        }
    }
} 