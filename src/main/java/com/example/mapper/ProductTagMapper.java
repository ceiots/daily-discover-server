package com.example.mapper;

import org.apache.ibatis.annotations.*;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.ProductTag;

import java.util.List;

@Mapper
public interface ProductTagMapper extends BaseMapper<ProductTag> {

    @Insert("INSERT INTO product_tag(product_id, tag_id, tag_name, tag_type, create_time) " +
            "VALUES(#{productId}, #{tagId}, #{tagName}, #{tagType}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductTag tag);
    
    @Select("SELECT * FROM product_tag WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "tagId", column = "tag_id"),
        @Result(property = "tagName", column = "tag_name"),
        @Result(property = "tagType", column = "tag_type"),
        @Result(property = "createTime", column = "create_time")
    })
    ProductTag findById(@Param("id") Long id);
    
    @Select("SELECT * FROM product_tag WHERE product_id = #{productId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "tagId", column = "tag_id"),
        @Result(property = "tagName", column = "tag_name"),
        @Result(property = "tagType", column = "tag_type"),
        @Result(property = "createTime", column = "create_time")
    })
    List<ProductTag> findByProductId(@Param("productId") Long productId);
    
    @Select("SELECT * FROM product_tag WHERE tag_id = #{tagId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "tagId", column = "tag_id"),
        @Result(property = "tagName", column = "tag_name"),
        @Result(property = "tagType", column = "tag_type"),
        @Result(property = "createTime", column = "create_time")
    })
    List<ProductTag> findByTagId(@Param("tagId") Long tagId);
    
    @Select("SELECT * FROM product_tag WHERE product_id = #{productId} AND tag_id = #{tagId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "tagId", column = "tag_id"),
        @Result(property = "tagName", column = "tag_name"),
        @Result(property = "tagType", column = "tag_type"),
        @Result(property = "createTime", column = "create_time")
    })
    ProductTag findByProductIdAndTagId(@Param("productId") Long productId, @Param("tagId") Long tagId);
    
    @Select("SELECT * FROM product_tag WHERE tag_type = #{tagType}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "tagId", column = "tag_id"),
        @Result(property = "tagName", column = "tag_name"),
        @Result(property = "tagType", column = "tag_type"),
        @Result(property = "createTime", column = "create_time")
    })
    List<ProductTag> findByTagType(@Param("tagType") Integer tagType);
    
    @Delete("DELETE FROM product_tag WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    @Delete("DELETE FROM product_tag WHERE product_id = #{productId}")
    int deleteByProductId(@Param("productId") Long productId);
    
    @Delete("DELETE FROM product_tag WHERE product_id = #{productId} AND tag_id = #{tagId}")
    int deleteByProductIdAndTagId(@Param("productId") Long productId, @Param("tagId") Long tagId);
    
    @Select("SELECT product_id FROM product_tag WHERE tag_id = #{tagId} LIMIT #{limit}")
    List<Long> findProductIdsByTagId(@Param("tagId") Long tagId, @Param("limit") Integer limit);
    
    @Select("SELECT COUNT(*) FROM product_tag WHERE tag_id = #{tagId}")
    int countByTagId(@Param("tagId") Long tagId);
} 