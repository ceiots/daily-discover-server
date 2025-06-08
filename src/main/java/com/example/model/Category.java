package com.example.model;

import java.util.Date;
import java.util.List;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 商品分类实体类
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Category {
    private Long id;
    private String name;        // 分类名称
    private String icon;        // 分类图标
    private Long parentId;      // 父级分类ID
    private Integer level;      // 分类层级:1-一级,2-二级,3-三级
    private Integer sortOrder;  // 排序权重
    private Boolean isVisible;  // 是否显示
    private Date createTime;
    private Date updateTime;
    
    // 非持久化字段
    private List<Category> children; // 子分类列表
    private Category parent;         // 父分类
} 