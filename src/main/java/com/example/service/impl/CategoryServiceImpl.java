package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dto.CategoryAddRequest;
import com.example.dto.CategoryQueryRequest;
import com.example.mapper.CategoryMapper;
import com.example.model.Category;
import com.example.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 商品分类服务实现类
 */
@Slf4j
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    @Override
    public List<Category> listCategories(CategoryQueryRequest categoryQueryRequest) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        System.out.println("categoryQueryRequest: " + categoryQueryRequest);
        
        // 构建查询条件
        if (categoryQueryRequest != null) {
            if (categoryQueryRequest.getName() != null && !categoryQueryRequest.getName().isEmpty()) {
                queryWrapper.like("name", categoryQueryRequest.getName());
            }
            
            if (categoryQueryRequest.getLevel() != null) {
                queryWrapper.eq("level", categoryQueryRequest.getLevel());
            }
            
            if (categoryQueryRequest.getStatus() != null) {
                queryWrapper.eq("status", categoryQueryRequest.getStatus());
            }
            
            if (categoryQueryRequest.getParentId() != null) {
                queryWrapper.eq("parent_id", categoryQueryRequest.getParentId());
            }
        }
        
        // 默认排序
        queryWrapper.orderByAsc("sort_order");
        
        return this.list(queryWrapper);
    }

    @Override
    public List<Category> getCategoryTree() {
        // 获取所有启用的分类
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getStatus, 1);
        queryWrapper.orderByAsc(Category::getSortOrder);
        List<Category> allCategories = this.list(queryWrapper);
        
        if (allCategories == null || allCategories.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 构建分类树
        Map<Long, List<Category>> parentIdToChildrenMap = allCategories.stream()
                .collect(Collectors.groupingBy(category -> 
                    category.getParentId() == null ? 0L : category.getParentId()));
        
        // 获取顶级分类
        List<Category> rootCategories = allCategories.stream()
                .filter(category -> category.getParentId() == null)
                .collect(Collectors.toList());
        
        // 递归设置子分类
        rootCategories.forEach(root -> setChildren(root, parentIdToChildrenMap));
        
        return rootCategories;
    }
    
    /**
     * 递归设置子分类
     *
     * @param parent 父分类
     * @param parentIdToChildrenMap 父ID到子分类的映射
     */
    private void setChildren(Category parent, Map<Long, List<Category>> parentIdToChildrenMap) {
        List<Category> children = parentIdToChildrenMap.get(parent.getId());
        if (children != null && !children.isEmpty()) {
            // 子分类按照sortOrder排序
            children.sort(Comparator.comparing(Category::getSortOrder));
            parent.setChildren(children);
            children.forEach(child -> setChildren(child, parentIdToChildrenMap));
        } else {
            parent.setChildren(Collections.emptyList());
        }
    }
} 