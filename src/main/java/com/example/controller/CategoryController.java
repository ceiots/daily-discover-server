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

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserIdExtractor userIdExtractor;

    /**
     * 创建商品分类
     *
     * @param categoryAddRequest 分类信息
     * @param request 请求对象
     * @return 通用结果，包含分类ID
     */
    @PostMapping
    public CommonResult<Long> addCategory(@RequestBody CategoryAddRequest categoryAddRequest,
                                         HttpServletRequest request) {
        if (categoryAddRequest == null) {
            return CommonResult.failed("请求参数不能为空");
        }
        
        Long userId = userIdExtractor.extractUserIdFromRequest(request);
        // 校验用户是否有权限
        if (userId == null) {
            return CommonResult.unauthorized(null);
        }

        try {
            long categoryId = categoryService.addCategory(categoryAddRequest, userId);
            return CommonResult.success(categoryId);
        } catch (Exception e) {
            log.error("添加分类失败", e);
            return CommonResult.failed("添加分类失败：" + e.getMessage());
        }
    }

    /**
     * 删除商品分类
     *
     * @param id 分类ID
     * @param request 请求对象
     * @return 通用结果，包含是否成功
     */
    @DeleteMapping("/{id}")
    public CommonResult<Boolean> deleteCategory(@PathVariable("id") Long id, HttpServletRequest request) {
        if (id <= 0) {
            return CommonResult.failed("无效的分类ID");
        }
        
        Long userId = userIdExtractor.extractUserIdFromRequest(request);
        // 校验用户是否有权限
        if (userId == null) {
            return CommonResult.unauthorized(null);
        }
        
        try {
            boolean result = categoryService.deleteCategory(id, userId);
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("删除分类失败", e);
            return CommonResult.failed("删除分类失败：" + e.getMessage());
        }
    }

    /**
     * 更新商品分类
     *
     * @param category 分类信息
     * @param request 请求对象
     * @return 通用结果，包含是否成功
     */
    @PutMapping
    public CommonResult<Boolean> updateCategory(@RequestBody Category category, HttpServletRequest request) {
        if (category == null || category.getId() <= 0) {
            return CommonResult.failed("无效的分类信息");
        }
        
        Long userId = userIdExtractor.extractUserIdFromRequest(request);
        // 校验用户是否有权限
        if (userId == null) {
            return CommonResult.unauthorized(null);
        }
        
        try {
            boolean result = categoryService.updateCategory(category, userId);
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("更新分类失败", e);
            return CommonResult.failed("更新分类失败：" + e.getMessage());
        }
    }

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

    /**
     * 获取商品分类列表
     *
     * @param categoryQueryRequest 查询条件
     * @return 通用结果，包含分类列表
     */
    @GetMapping
    public CommonResult<List<Category>> listCategories(CategoryQueryRequest categoryQueryRequest) {
        try {
            if (categoryQueryRequest == null) {
                categoryQueryRequest = new CategoryQueryRequest();
            }
            
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