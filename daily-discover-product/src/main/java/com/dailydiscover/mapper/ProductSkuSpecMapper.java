package com.dailydiscover.mapper;

import com.dailydiscover.model.ProductSkuSpec;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;

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
    
    /**
     * 根据商品ID查询规格定义
     */
    @Select("SELECT * FROM product_sku_specs WHERE product_id = #{productId} ORDER BY spec_order ASC")
    List<ProductSkuSpec> getSpecsByProductId(@Param("productId") Long productId);
    
    /**
     * 创建商品规格定义
     */
    @Insert("INSERT INTO product_sku_specs (product_id, spec_name, is_required, spec_order, created_at) " +
            "VALUES (#{productId}, #{specName}, #{isRequired}, (SELECT COALESCE(MAX(spec_order), 0) + 1 FROM product_sku_specs WHERE product_id = #{productId}), NOW())")
    int createSpec(@Param("productId") Long productId, @Param("specName") String specName, @Param("isRequired") Boolean isRequired);
    
    /**
     * 更新规格排序
     */
    @Update("UPDATE product_sku_specs SET spec_order = #{sortOrder}, updated_at = NOW() WHERE id = #{specId}")
    int updateSpecSortOrder(@Param("specId") Long specId, @Param("sortOrder") Integer sortOrder);
}