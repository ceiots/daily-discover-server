package com.dailydiscover.mapper;

import com.dailydiscover.model.ProductTagRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商品标签关联表 Mapper
 */
@Mapper
public interface ProductTagRelationMapper extends BaseMapper<ProductTagRelation> {
    
    /**
     * 根据商品ID查询标签关联
     */
    @Select("SELECT * FROM product_tag_relations WHERE product_id = #{productId}")
    List<ProductTagRelation> findByProductId(@Param("productId") Long productId);
    
    /**
     * 根据标签ID查询商品关联
     */
    @Select("SELECT * FROM product_tag_relations WHERE tag_id = #{tagId}")
    List<ProductTagRelation> findByTagId(@Param("tagId") Long tagId);
}