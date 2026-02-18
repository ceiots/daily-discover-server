package com.dailydiscover.mapper;

import com.dailydiscover.model.ProductInventoryCore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 库存核心表 Mapper
 */
@Mapper
public interface ProductInventoryCoreMapper extends BaseMapper<ProductInventoryCore> {
    
    /**
     * 根据SKU ID查询库存信息
     */
    @Select("SELECT * FROM product_inventory_core WHERE sku_id = #{skuId}")
    ProductInventoryCore findBySkuId(@Param("skuId") Long skuId);
    
    /**
     * 根据商品ID查询所有SKU的库存信息
     */
    @Select("SELECT pic.* FROM product_inventory_core pic " +
            "JOIN product_skus ps ON pic.sku_id = ps.id " +
            "WHERE ps.product_id = #{productId}")
    List<ProductInventoryCore> findByProductId(@Param("productId") Long productId);
}