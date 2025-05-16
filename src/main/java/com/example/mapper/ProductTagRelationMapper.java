package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.ProductTagRelation;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商品-标签关联数据库操作
 */
public interface ProductTagRelationMapper extends BaseMapper<ProductTagRelation> {

    /**
     * 根据商品ID获取标签ID列表
     *
     * @param productId 商品ID
     * @return 标签ID列表
     */
    @Select("SELECT tag_id FROM product_tag_relations WHERE product_id = #{productId}")
    List<Long> getTagIdsByProductId(@Param("productId") Long productId);

    /**
     * 根据标签ID获取商品ID列表
     *
     * @param tagId 标签ID
     * @return 商品ID列表
     */
    @Select("SELECT product_id FROM product_tag_relations WHERE tag_id = #{tagId}")
    List<Long> getProductIdsByTagId(@Param("tagId") Long tagId);
} 