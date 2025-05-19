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