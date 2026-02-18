package com.dailydiscover.mapper;

import com.dailydiscover.model.ProductTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商品标签表 Mapper
 */
@Mapper
public interface ProductTagMapper extends BaseMapper<ProductTag> {
    
    /**
     * 根据标签名称查询标签
     */
    @Select("SELECT * FROM product_tags WHERE tag_name = #{tagName}")
    ProductTag findByTagName(@Param("tagName") String tagName);
    
    /**
     * 查询热门标签
     */
    @Select("SELECT * FROM product_tags ORDER BY usage_count DESC LIMIT #{limit}")
    List<ProductTag> findPopularTags(@Param("limit") int limit);
}