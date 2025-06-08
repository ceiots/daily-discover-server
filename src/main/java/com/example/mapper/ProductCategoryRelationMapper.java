package com.example.mapper;

import org.apache.ibatis.annotations.*;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.ProductCategoryRelation;
import com.example.model.Category;

import java.util.List;

@Mapper
public interface ProductCategoryRelationMapper extends BaseMapper<ProductCategoryRelation> {

    @Insert("INSERT INTO product_category_relation(product_id, category_id, parent_category_id, " +
            "grand_category_id, is_primary, sort_weight, create_time) " +
            "VALUES(#{productId}, #{categoryId}, #{parentCategoryId}, " +
            "#{grandCategoryId}, #{isPrimary}, #{sortWeight}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductCategoryRelation relation);
    
    @Select("SELECT pcr.*, c.name as category_name, pc.name as parent_category_name, gc.name as grand_category_name " +
            "FROM product_category_relation pcr " +
            "LEFT JOIN product_categories c ON pcr.category_id = c.id " +
            "LEFT JOIN product_categories pc ON pcr.parent_category_id = pc.id " +
            "LEFT JOIN product_categories gc ON pcr.grand_category_id = gc.id " +
            "WHERE pcr.id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "parentCategoryId", column = "parent_category_id"),
        @Result(property = "grandCategoryId", column = "grand_category_id"),
        @Result(property = "isPrimary", column = "is_primary"),
        @Result(property = "sortWeight", column = "sort_weight"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "category", column = "category_id", 
                one = @One(select = "com.example.mapper.CategoryMapper.findById")),
        @Result(property = "parentCategory", column = "parent_category_id", 
                one = @One(select = "com.example.mapper.CategoryMapper.findById")),
        @Result(property = "grandCategory", column = "grand_category_id", 
                one = @One(select = "com.example.mapper.CategoryMapper.findById"))
    })
    ProductCategoryRelation findById(@Param("id") Long id);
    
    @Select("SELECT pcr.*, c.name as category_name, pc.name as parent_category_name, gc.name as grand_category_name " +
            "FROM product_category_relation pcr " +
            "LEFT JOIN product_categories c ON pcr.category_id = c.id " +
            "LEFT JOIN product_categories pc ON pcr.parent_category_id = pc.id " +
            "LEFT JOIN product_categories gc ON pcr.grand_category_id = gc.id " +
            "WHERE pcr.product_id = #{productId} " +
            "ORDER BY pcr.is_primary DESC, pcr.sort_weight DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "parentCategoryId", column = "parent_category_id"),
        @Result(property = "grandCategoryId", column = "grand_category_id"),
        @Result(property = "isPrimary", column = "is_primary"),
        @Result(property = "sortWeight", column = "sort_weight"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "category", column = "category_id", 
                one = @One(select = "com.example.mapper.CategoryMapper.findById")),
        @Result(property = "parentCategory", column = "parent_category_id", 
                one = @One(select = "com.example.mapper.CategoryMapper.findById")),
        @Result(property = "grandCategory", column = "grand_category_id", 
                one = @One(select = "com.example.mapper.CategoryMapper.findById"))
    })
    List<ProductCategoryRelation> findByProductId(@Param("productId") Long productId);
    
    @Select("SELECT pcr.*, c.name as category_name, pc.name as parent_category_name, gc.name as grand_category_name " +
            "FROM product_category_relation pcr " +
            "LEFT JOIN product_categories c ON pcr.category_id = c.id " +
            "LEFT JOIN product_categories pc ON pcr.parent_category_id = pc.id " +
            "LEFT JOIN product_categories gc ON pcr.grand_category_id = gc.id " +
            "WHERE pcr.category_id = #{categoryId} " +
            "ORDER BY pcr.is_primary DESC, pcr.sort_weight DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "parentCategoryId", column = "parent_category_id"),
        @Result(property = "grandCategoryId", column = "grand_category_id"),
        @Result(property = "isPrimary", column = "is_primary"),
        @Result(property = "sortWeight", column = "sort_weight"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "category", column = "category_id", 
                one = @One(select = "com.example.mapper.CategoryMapper.findById")),
        @Result(property = "parentCategory", column = "parent_category_id", 
                one = @One(select = "com.example.mapper.CategoryMapper.findById")),
        @Result(property = "grandCategory", column = "grand_category_id", 
                one = @One(select = "com.example.mapper.CategoryMapper.findById"))
    })
    List<ProductCategoryRelation> findByCategoryId(@Param("categoryId") Long categoryId);
    
    @Select("SELECT pcr.*, c.name as category_name, pc.name as parent_category_name, gc.name as grand_category_name " +
            "FROM product_category_relation pcr " +
            "LEFT JOIN product_categories c ON pcr.category_id = c.id " +
            "LEFT JOIN product_categories pc ON pcr.parent_category_id = pc.id " +
            "LEFT JOIN product_categories gc ON pcr.grand_category_id = gc.id " +
            "WHERE pcr.product_id = #{productId} AND pcr.is_primary = 1 " +
            "LIMIT 1")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "parentCategoryId", column = "parent_category_id"),
        @Result(property = "grandCategoryId", column = "grand_category_id"),
        @Result(property = "isPrimary", column = "is_primary"),
        @Result(property = "sortWeight", column = "sort_weight"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "category", column = "category_id", 
                one = @One(select = "com.example.mapper.CategoryMapper.findById")),
        @Result(property = "parentCategory", column = "parent_category_id", 
                one = @One(select = "com.example.mapper.CategoryMapper.findById")),
        @Result(property = "grandCategory", column = "grand_category_id", 
                one = @One(select = "com.example.mapper.CategoryMapper.findById"))
    })
    ProductCategoryRelation findPrimaryByProductId(@Param("productId") Long productId);
    
    @Update("UPDATE product_category_relation SET " +
            "is_primary = #{isPrimary}, " +
            "sort_weight = #{sortWeight} " +
            "WHERE id = #{id}")
    int update(ProductCategoryRelation relation);
    
    @Delete("DELETE FROM product_category_relation WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    @Delete("DELETE FROM product_category_relation WHERE product_id = #{productId}")
    int deleteByProductId(@Param("productId") Long productId);
    
    @Select("SELECT COUNT(*) FROM product_category_relation WHERE category_id = #{categoryId}")
    int countByCategoryId(@Param("categoryId") Long categoryId);
} 