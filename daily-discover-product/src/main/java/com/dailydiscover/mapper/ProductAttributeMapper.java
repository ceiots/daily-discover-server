package com.dailydiscover.mapper;

import com.dailydiscover.model.ProductAttribute;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ProductAttributeMapper {
    @Select("SELECT * FROM product_attributes WHERE product_id = #{productId}")
    ProductAttribute findByProductId(Long productId);
    
    @Insert("INSERT INTO product_attributes (product_id, is_new, is_hot, is_recommended, urgency_level, hotspot_type, tags) " +
            "VALUES (#{productId}, #{isNew}, #{isHot}, #{isRecommended}, #{urgencyLevel}, #{hotspotType}, #{tags})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ProductAttribute productAttribute);
    
    @Update("UPDATE product_attributes SET is_new = #{isNew}, is_hot = #{isHot}, is_recommended = #{isRecommended}, " +
            "urgency_level = #{urgencyLevel}, hotspot_type = #{hotspotType}, tags = #{tags}, updated_at = CURRENT_TIMESTAMP " +
            "WHERE product_id = #{productId}")
    void update(ProductAttribute productAttribute);
    
    @Delete("DELETE FROM product_attributes WHERE product_id = #{productId}")
    void deleteByProductId(Long productId);
}