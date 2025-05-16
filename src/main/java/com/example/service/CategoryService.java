package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dto.CategoryAddRequest;
import com.example.dto.CategoryQueryRequest;
import com.example.model.Category;

import java.util.List;

/**
 * 商品分类服务
 */
public interface CategoryService extends IService<Category> {

    /**
     * 添加分类
     *
     * @param categoryAddRequest 分类信息
     * @param userId            创建人ID
     * @return 分类ID
     */
    long addCategory(CategoryAddRequest categoryAddRequest, Long userId);

    /**
     * 删除分类
     *
     * @param id     分类ID
     * @param userId 操作人ID
     * @return 是否成功
     */
    boolean deleteCategory(Long id, Long userId);

    /**
     * 更新分类
     *
     * @param category 分类信息
     * @param userId   操作人ID
     * @return 是否成功
     */
    boolean updateCategory(Category category, Long userId);

    /**
     * 获取分类列表
     *
     * @param categoryQueryRequest 查询条件
     * @return 分类列表
     */
    List<Category> listCategories(CategoryQueryRequest categoryQueryRequest);

    /**
     * 获取分类树
     *
     * @return 分类树
     */
    List<Category> getCategoryTree();
} 