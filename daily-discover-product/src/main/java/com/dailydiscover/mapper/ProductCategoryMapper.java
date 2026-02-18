package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.ProductCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductCategoryMapper extends BaseMapper<ProductCategory> {
    
    /**
     * 根据父分类ID查询子分类
     */
    @Select("SELECT * FROM product_categories WHERE parent_id = #{parentId} AND status = 1 ORDER BY sort_order ASC")
    List<ProductCategory> findByParentId(@Param("parentId") Long parentId);
    
    /**
     * 查询所有启用状态的分类
     */
    @Select("SELECT * FROM product_categories WHERE status = 1 ORDER BY sort_order ASC")
    List<ProductCategory> findAllActiveCategories();
    
    /**
     * 根据分类级别查询分类
     */
    @Select("SELECT * FROM product_categories WHERE level = #{level} AND status = 1 ORDER BY sort_order ASC")
    List<ProductCategory> findByLevel(@Param("level") Integer level);
    
    /**
     * 根据分类名称模糊查询
     */
    @Select("SELECT * FROM product_categories WHERE name LIKE CONCAT('%', #{name}, '%') AND status = 1 ORDER BY sort_order ASC")
    List<ProductCategory> findByNameLike(@Param("name") String name);
    
    /**
     * 查询根分类
     */
    @Select("SELECT * FROM product_categories WHERE parent_id IS NULL AND status = 1 ORDER BY sort_order ASC")
    List<ProductCategory> findRootCategories();
}