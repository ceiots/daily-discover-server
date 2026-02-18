package com.dailydiscover.mapper;

import com.dailydiscover.model.ProductSkuSpecOption;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商品规格选项表 Mapper
 */
@Mapper
public interface ProductSkuSpecOptionMapper extends BaseMapper<ProductSkuSpecOption> {
    
    /**
     * 根据规格ID查询选项
     */
    @Select("SELECT * FROM product_sku_spec_options WHERE spec_id = #{specId} ORDER BY option_order ASC")
    List<ProductSkuSpecOption> findBySpecId(@Param("specId") Long specId);
    
    /**
     * 根据商品ID查询所有规格选项
     */
    @Select("SELECT pso.* FROM product_sku_spec_options pso " +
            "JOIN product_sku_specs pss ON pso.spec_id = pss.id " +
            "WHERE pss.product_id = #{productId} ORDER BY pss.spec_order ASC, pso.option_order ASC")
    List<ProductSkuSpecOption> findByProductId(@Param("productId") Long productId);
}