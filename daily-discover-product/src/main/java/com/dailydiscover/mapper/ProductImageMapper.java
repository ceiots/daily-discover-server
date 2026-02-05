package com.dailydiscover.mapper;

import com.dailydiscover.model.ProductImage;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductImageMapper {
    @Select("SELECT * FROM product_images WHERE product_id = #{productId} ORDER BY sort_order ASC")
    List<ProductImage> findByProductId(Long productId);
    
    @Select("SELECT * FROM product_images WHERE product_id = #{productId} AND image_type = #{imageType} ORDER BY sort_order ASC")
    List<ProductImage> findByProductIdAndType(@Param("productId") Long productId, @Param("imageType") String imageType);
    
    @Select("SELECT * FROM product_images WHERE product_id = #{productId} AND is_primary = true")
    ProductImage findPrimaryImage(Long productId);
    
    @Insert("INSERT INTO product_images (product_id, image_type, image_url, alt_text, sort_order, is_primary) " +
            "VALUES (#{productId}, #{imageType}, #{imageUrl}, #{altText}, #{sortOrder}, #{isPrimary})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ProductImage productImage);
    
    @Update("UPDATE product_images SET image_type = #{imageType}, image_url = #{imageUrl}, alt_text = #{altText}, sort_order = #{sortOrder}, is_primary = #{isPrimary} WHERE id = #{id}")
    void update(ProductImage productImage);
    
    @Delete("DELETE FROM product_images WHERE id = #{id}")
    void delete(Long id);
    
    @Delete("DELETE FROM product_images WHERE product_id = #{productId}")
    void deleteByProductId(Long productId);
}