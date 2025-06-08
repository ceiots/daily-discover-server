package com.example.model;

import java.util.Date;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 商品分类关联实体类
 * 用于处理商品与分类的多对多关系
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductCategoryRelation {
    private Long id;
    private Long productId;
    private Long categoryId;
    private Long parentCategoryId;     // 父级分类ID
    private Long grandCategoryId;      // 祖父级分类ID
    private Boolean isPrimary;         // 是否主分类
    private Integer sortWeight;        // 排序权重
    private Date createTime;
    
    // 非持久化字段，用于API展示
    private transient Category category;
    private transient Category parentCategory;
    private transient Category grandCategory;
    
    /**
     * 创建商品分类关联
     */
    public static ProductCategoryRelation create(Long productId, Long categoryId, 
            Long parentCategoryId, Long grandCategoryId, Boolean isPrimary, Integer sortWeight) {
        
        ProductCategoryRelation relation = new ProductCategoryRelation();
        relation.setProductId(productId);
        relation.setCategoryId(categoryId);
        relation.setParentCategoryId(parentCategoryId);
        relation.setGrandCategoryId(grandCategoryId);
        relation.setIsPrimary(isPrimary != null ? isPrimary : true);
        relation.setSortWeight(sortWeight != null ? sortWeight : 0);
        relation.setCreateTime(new Date());
        
        return relation;
    }
} 