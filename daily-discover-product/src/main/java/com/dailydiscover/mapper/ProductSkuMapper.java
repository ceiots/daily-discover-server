package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.ProductSku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductSkuMapper extends BaseMapper<ProductSku> {
    
    /**
     * 根据商品ID查询SKU列表
     */
    @Select("SELECT * FROM product_skus WHERE product_id = #{productId} AND is_deleted = 0 ORDER BY sort_order ASC")
    List<ProductSku> findByProductId(@Param("productId") Long productId);
    
    /**
     * 根据SKU ID查询SKU信息
     */
    @Select("SELECT * FROM product_skus WHERE id = #{skuId} AND is_deleted = 0")
    ProductSku findById(@Param("skuId") Long skuId);
    
    /**
     * 根据SKU编码查询SKU信息
     */
    @Select("SELECT * FROM product_skus WHERE sku_code = #{skuCode} AND is_deleted = 0")
    ProductSku findBySkuCode(@Param("skuCode") String skuCode);
    
    /**
     * 更新SKU库存
     */
    @Select("UPDATE product_skus SET stock = stock - #{quantity}, updated_at = NOW() WHERE id = #{skuId} AND stock >= #{quantity}")
    int updateStock(@Param("skuId") Long skuId, @Param("quantity") Integer quantity);
}