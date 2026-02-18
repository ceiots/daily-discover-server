package com.dailydiscover.mapper;

import com.dailydiscover.model.ProductInventoryConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 库存配置表 Mapper
 */
@Mapper
public interface ProductInventoryConfigMapper extends BaseMapper<ProductInventoryConfig> {
    
    /**
     * 根据SKU ID查询库存配置
     */
    @Select("SELECT * FROM product_inventory_config WHERE sku_id = #{skuId}")
    ProductInventoryConfig findBySkuId(@Param("skuId") Long skuId);
}