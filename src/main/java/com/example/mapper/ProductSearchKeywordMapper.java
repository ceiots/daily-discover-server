package com.example.mapper;

import org.apache.ibatis.annotations.*;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.ProductSearchKeyword;

import java.util.List;

@Mapper
public interface ProductSearchKeywordMapper extends BaseMapper<ProductSearchKeyword> {

    @Insert("INSERT INTO product_search_keyword(product_id, keyword, weight, is_manual) " +
            "VALUES(#{productId}, #{keyword}, #{weight}, #{isManual})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductSearchKeyword keyword);
    
    @Select("SELECT * FROM product_search_keyword WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "keyword", column = "keyword"),
        @Result(property = "weight", column = "weight"),
        @Result(property = "isManual", column = "is_manual"),
        @Result(property = "createTime", column = "create_time")
    })
    ProductSearchKeyword findById(@Param("id") Long id);
    
    @Select("SELECT * FROM product_search_keyword WHERE product_id = #{productId} ORDER BY weight DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "keyword", column = "keyword"),
        @Result(property = "weight", column = "weight"),
        @Result(property = "isManual", column = "is_manual"),
        @Result(property = "createTime", column = "create_time")
    })
    List<ProductSearchKeyword> findByProductId(@Param("productId") Long productId);
    
    @Select("SELECT * FROM product_search_keyword WHERE keyword LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY weight DESC LIMIT #{limit}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "keyword", column = "keyword"),
        @Result(property = "weight", column = "weight"),
        @Result(property = "isManual", column = "is_manual"),
        @Result(property = "createTime", column = "create_time")
    })
    List<ProductSearchKeyword> findByKeyword(@Param("keyword") String keyword, @Param("limit") Integer limit);
    
    @Update("UPDATE product_search_keyword SET " +
            "keyword = #{keyword}, " +
            "weight = #{weight}, " +
            "is_manual = #{isManual} " +
            "WHERE id = #{id}")
    int update(ProductSearchKeyword keyword);
    
    @Delete("DELETE FROM product_search_keyword WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    @Delete("DELETE FROM product_search_keyword WHERE product_id = #{productId}")
    int deleteByProductId(@Param("productId") Long productId);
    
    @Select("SELECT DISTINCT product_id FROM product_search_keyword WHERE keyword LIKE CONCAT('%', #{keyword}, '%') " +
            "ORDER BY weight DESC LIMIT #{limit}")
    List<Long> findProductIdsByKeyword(@Param("keyword") String keyword, @Param("limit") Integer limit);
} 