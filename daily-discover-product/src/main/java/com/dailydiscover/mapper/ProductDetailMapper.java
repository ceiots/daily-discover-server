package com.dailydiscover.mapper;

import com.dailydiscover.model.ProductDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 商品详情表 Mapper
 */
@Mapper
public interface ProductDetailMapper extends BaseMapper<ProductDetail> {
    
    /**
     * 根据商品ID查询商品详情
     */
    @Select("SELECT * FROM product_details WHERE product_id = #{productId}")
    ProductDetail findByProductId(@Param("productId") Long productId);
}