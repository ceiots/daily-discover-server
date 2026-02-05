package com.dailydiscover.mapper;

import com.dailydiscover.model.ProductDetail;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ProductDetailMapper {
    @Select("SELECT * FROM product_details WHERE product_id = #{productId}")
    ProductDetail findByProductId(Long productId);
    
    @Insert("INSERT INTO product_details (product_id, specifications, features, usage_instructions, precautions, package_contents, warranty_info, shipping_info) " +
            "VALUES (#{productId}, #{specifications}, #{features}, #{usageInstructions}, #{precautions}, #{packageContents}, #{warrantyInfo}, #{shippingInfo})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ProductDetail productDetail);
    
    @Update("UPDATE product_details SET specifications = #{specifications}, features = #{features}, usage_instructions = #{usageInstructions}, precautions = #{precautions}, package_contents = #{packageContents}, warranty_info = #{warrantyInfo}, shipping_info = #{shippingInfo}, updated_at = CURRENT_TIMESTAMP WHERE product_id = #{productId}")
    void update(ProductDetail productDetail);
    
    @Delete("DELETE FROM product_details WHERE product_id = #{productId}")
    void deleteByProductId(Long productId);
}