package com.dailydiscover.mapper;

import com.dailydiscover.model.ProductInventoryConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 库存配置表 Mapper
 */
@Mapper
public interface ProductInventoryConfigMapper extends BaseMapper<ProductInventoryConfig> {
    
    /**
     * 根据库存名称查询库存配置
     */
    @Select("SELECT * FROM product_inventory_config WHERE inventory_name = #{inventoryName}")
    ProductInventoryConfig findByInventoryName(@Param("inventoryName") String inventoryName);
    
    /**
     * 根据商品ID查询库存配置
     */
    @Select("SELECT * FROM product_inventory_config WHERE product_id = #{productId}")
    ProductInventoryConfig findByProductId(@Param("productId") Long productId);
    
    /**
     * 根据库存编码查询库存配置
     */
    @Select("SELECT * FROM product_inventory_config WHERE inventory_code = #{inventoryCode}")
    ProductInventoryConfig findByInventoryCode(@Param("inventoryCode") String inventoryCode);
    
    /**
     * 查询需要补货的库存配置
     */
    @Select("SELECT * FROM product_inventory_config WHERE next_restock_date IS NOT NULL")
    List<ProductInventoryConfig> findInventoriesNeedRestock();
    
    /**
     * 批量更新库存配置
     */
    @Update("<script>" +
            "UPDATE product_inventory_config SET ${configKey} = #{configValue} WHERE inventory_id IN " +
            "<foreach collection='inventoryIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    int batchUpdateConfig(@Param("inventoryIds") List<Long> inventoryIds, 
                         @Param("configKey") String configKey, 
                         @Param("configValue") String configValue);
}