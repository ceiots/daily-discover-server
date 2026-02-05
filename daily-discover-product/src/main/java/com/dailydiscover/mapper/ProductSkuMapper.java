package com.dailydiscover.mapper;

import com.dailydiscover.model.ProductSku;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductSkuMapper {
    @Select("SELECT * FROM product_skus WHERE product_id = #{productId} AND is_active = true")
    List<ProductSku> findByProductId(Long productId);
    
    @Select("SELECT * FROM product_skus WHERE sku_code = #{skuCode}")
    ProductSku findBySkuCode(String skuCode);
    
    @Select("SELECT * FROM product_skus WHERE product_id = #{productId} AND is_default = true")
    ProductSku findDefaultSku(Long productId);
    
    @Insert("INSERT INTO product_skus (product_id, sku_code, sku_name, price, original_price, stock_quantity, reserved_quantity, available_quantity, spec_combination, weight, volume, barcode, is_default, is_active) " +
            "VALUES (#{productId}, #{skuCode}, #{skuName}, #{price}, #{originalPrice}, #{stockQuantity}, #{reservedQuantity}, #{availableQuantity}, #{specCombination}, #{weight}, #{volume}, #{barcode}, #{isDefault}, #{isActive})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(ProductSku productSku);
    
    @Update("UPDATE product_skus SET sku_name = #{skuName}, price = #{price}, original_price = #{originalPrice}, stock_quantity = #{stockQuantity}, reserved_quantity = #{reservedQuantity}, available_quantity = #{availableQuantity}, spec_combination = #{specCombination}, weight = #{weight}, volume = #{volume}, barcode = #{barcode}, is_default = #{isDefault}, is_active = #{isActive}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void update(ProductSku productSku);
    
    @Update("UPDATE product_skus SET is_active = false WHERE id = #{id}")
    void deactivate(Long id);
}