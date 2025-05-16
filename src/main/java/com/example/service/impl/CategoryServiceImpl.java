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
    @Transactional
    public long addCategory(CategoryAddRequest categoryAddRequest, Long userId) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryAddRequest, category);
        
        category.setUserId(userId);
        
        // 如果未指定状态，默认为启用
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        
        // 设置创建时间和更新时间
        Date now = new Date();
        category.setCreatedAt(now);
        category.setUpdatedAt(now);
        
        // 如果是顶级分类，确保parentId为null
        if (category.getLevel() != null && category.getLevel() == 1) {
            category.setParentId(null);
        }
        
        // 如果指定了parentId，验证父分类是否存在
        if (category.getParentId() != null && category.getParentId() > 0) {
            Category parentCategory = this.getById(category.getParentId());
            if (parentCategory == null) {
                throw new RuntimeException("父分类不存在");
            }
            
            // 设置正确的层级
            category.setLevel(parentCategory.getLevel() + 1);
        }
        
        // 保存分类
        boolean success = this.save(category);
        if (success) {
            return category.getId();
        } else {
            throw new RuntimeException("添加分类失败");
        }
    }

    @Override
    @Transactional
    public boolean deleteCategory(Long id, Long userId) {
        if (id == null || id <= 0) {
            throw new RuntimeException("无效的分类ID");
        }
        
        // 查询是否有子分类
        LambdaQueryWrapper<Category> childrenWrapper = new LambdaQueryWrapper<>();
        childrenWrapper.eq(Category::getParentId, id);
        long childrenCount = this.count(childrenWrapper);
        if (childrenCount > 0) {
            throw new RuntimeException("该分类下有子分类，无法删除");
        }
        
        // TODO: 也应该检查是否有关联的商品，如果有则不能删除
        
        // 删除分类
        return this.removeById(id);
    }

    @Override
    @Transactional
    public boolean updateCategory(Category category, Long userId) {
        if (category == null || category.getId() == null || category.getId() <= 0) {
            throw new RuntimeException("无效的分类ID");
        }
        
        Category existingCategory = this.getById(category.getId());
        if (existingCategory == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 如果修改了parentId，需要验证父分类是否存在
        if (category.getParentId() != null && !category.getParentId().equals(existingCategory.getParentId())) {
            if (category.getParentId() > 0) {
                Category parentCategory = this.getById(category.getParentId());
                if (parentCategory == null) {
                    throw new RuntimeException("父分类不存在");
                }
                
                // 设置正确的层级
                category.setLevel(parentCategory.getLevel() + 1);
            } else {
                // 如果变为顶级分类
                category.setParentId(null);
                category.setLevel(1);
            }
        }
        
        // 更新时间
        category.setUpdatedAt(new Date());
        
        // 保留创建时间和创建用户ID
        category.setCreatedAt(existingCategory.getCreatedAt());
        category.setUserId(existingCategory.getUserId());
        
        // 更新分类
        return this.updateById(category);
    }

    @Override
    public List<Category> listCategories(CategoryQueryRequest categoryQueryRequest) {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        
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