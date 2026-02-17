package com.dailydiscover.mapper;

import com.dailydiscover.model.ProductImage;
import com.dailydiscover.model.ImageCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商品图片Mapper
 */
@Mapper
public interface ProductImageMapper {
    
    /**
     * 根据商品ID查询图片
     */
    @Select("SELECT * FROM product_images WHERE product_id = #{productId} AND is_visible = true ORDER BY display_order, sort_order")
    List<ProductImage> findByProductId(@Param("productId") Long productId);
    
    /**
     * 根据分类ID查询分类信息
     */
    @Select("SELECT * FROM image_categories WHERE id = #{categoryId} AND is_active = true")
    ImageCategory findCategoryById(@Param("categoryId") Long categoryId);
    
    /**
     * 查询所有启用的图片分类
     */
    @Select("SELECT * FROM image_categories WHERE is_active = true ORDER BY sort_order")
    List<ImageCategory> findAllActiveCategories();
    
    /**
     * 根据分类类型查询分类
     */
    @Select("SELECT * FROM image_categories WHERE category_type = #{categoryType} AND is_active = true ORDER BY sort_order")
    List<ImageCategory> findByCategoryType(@Param("categoryType") String categoryType);
}