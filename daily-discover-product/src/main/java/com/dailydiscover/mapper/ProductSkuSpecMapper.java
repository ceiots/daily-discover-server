package com.dailydiscover.mapper;

import com.dailydiscover.model.ProductSkuSpec;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商品规格定义表 Mapper
 */
@Mapper
public interface ProductSkuSpecMapper extends BaseMapper<ProductSkuSpec> {
    
    /**
     * 根据商品ID查询规格定义
     */
    @Select("SELECT * FROM product_sku_specs WHERE product_id = #{productId} ORDER BY spec_order ASC")
    List<ProductSkuSpec> findByProductId(@Param("productId") Long productId);
}