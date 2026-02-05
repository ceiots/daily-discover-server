package com.dailydiscover.mapper;

import com.dailydiscover.model.ProductSpec;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductSpecMapper {
    @Select("SELECT * FROM product_specs WHERE product_id = #{productId} ORDER BY sort_order ASC")
    List<ProductSpec> findByProductId(Long productId);
    
    @Select("SELECT * FROM product_specs WHERE product_id = #{productId} AND spec_group = #{specGroup} ORDER BY sort_order ASC")
    List<ProductSpec> findByProductIdAndGroup(@Param("productId") Long productId, @Param("specGroup") String specGroup);
    
    @Insert("INSERT INTO product_specs (product_id, spec_name, spec_label, spec_value, spec_unit, spec_group, sort_order) " +
            "VALUES (#{productId}, #{specName}, #{specLabel}, #{specValue}, #{specUnit}, #{specGroup}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ProductSpec productSpec);
    
    @Update("UPDATE product_specs SET spec_name = #{specName}, spec_label = #{specLabel}, spec_value = #{specValue}, spec_unit = #{specUnit}, spec_group = #{specGroup}, sort_order = #{sortOrder} WHERE id = #{id}")
    void update(ProductSpec productSpec);
    
    @Delete("DELETE FROM product_specs WHERE id = #{id}")
    void delete(Long id);
    
    @Delete("DELETE FROM product_specs WHERE product_id = #{productId}")
    void deleteByProductId(Long productId);
}