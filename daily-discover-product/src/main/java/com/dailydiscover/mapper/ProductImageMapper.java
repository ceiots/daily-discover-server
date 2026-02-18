package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.ProductImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * 产品图片表 Mapper
 */
@Mapper
public interface ProductImageMapper extends BaseMapper<ProductImage> {
    
    @Select("SELECT * FROM product_images WHERE product_id = #{productId} ORDER BY sort_order ASC")
    List<ProductImage> findByProductId(@Param("productId") Long productId);
    
    @Select("SELECT * FROM product_images WHERE product_id = #{productId} AND is_primary = true LIMIT 1")
    ProductImage findPrimaryImageByProductId(@Param("productId") Long productId);
}