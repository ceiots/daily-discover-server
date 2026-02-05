package com.dailydiscover.mapper;

import com.dailydiscover.model.ProductCategory;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductCategoryMapper {
    @Select("SELECT * FROM product_categories WHERE id = #{id}")
    ProductCategory findById(Long id);
    
    @Select("SELECT * FROM product_categories WHERE is_active = true ORDER BY sort_order ASC")
    List<ProductCategory> findAll();
    
    @Select("SELECT * FROM product_categories WHERE parent_id = #{parentId} AND is_active = true ORDER BY sort_order ASC")
    List<ProductCategory> findByParentId(Long parentId);
    
    @Select("SELECT * FROM product_categories WHERE level = #{level} AND is_active = true ORDER BY sort_order ASC")
    List<ProductCategory> findByLevel(Integer level);
    
    @Insert("INSERT INTO product_categories (parent_id, name, description, image_url, sort_order, level, is_active) " +
            "VALUES (#{parentId}, #{name}, #{description}, #{imageUrl}, #{sortOrder}, #{level}, #{isActive})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ProductCategory category);
    
    @Update("UPDATE product_categories SET parent_id = #{parentId}, name = #{name}, description = #{description}, image_url = #{imageUrl}, sort_order = #{sortOrder}, level = #{level}, is_active = #{isActive}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void update(ProductCategory category);
    
    @Update("UPDATE product_categories SET is_active = false WHERE id = #{id}")
    void deactivate(Long id);
}