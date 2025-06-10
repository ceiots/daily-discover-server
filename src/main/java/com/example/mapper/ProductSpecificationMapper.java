package com.example.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.common.util.JsonTypeHandler;
import com.example.model.ProductSpecification;

@Mapper
public interface ProductSpecificationMapper extends BaseMapper<ProductSpecification> {

    @Insert("INSERT INTO product_specification(product_id, specifications, attributes, has_multiple_skus) " +
            "VALUES(#{productId}, #{specifications,typeHandler=com.example.util.JsonTypeHandler}, " +
            "#{attributes,typeHandler=com.example.util.JsonTypeHandler}, #{hasMultipleSkus})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductSpecification spec);
    
    @Select("SELECT * FROM product_specification WHERE product_id = #{productId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "specifications", column = "specifications", 
                typeHandler = JsonTypeHandler.class),
        @Result(property = "attributes", column = "attributes", 
                typeHandler = JsonTypeHandler.class),
        @Result(property = "hasMultipleSkus", column = "has_multiple_skus"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    ProductSpecification findByProductId(@Param("productId") Long productId);
    
    @Update("UPDATE product_specification SET " +
            "specifications = #{specifications,typeHandler=com.example.util.JsonTypeHandler}, " +
            "attributes = #{attributes,typeHandler=com.example.util.JsonTypeHandler}, " +
            "has_multiple_skus = #{hasMultipleSkus}, " +
            "update_time = NOW() " +
            "WHERE product_id = #{productId}")
    int updateByProductId(ProductSpecification spec);
    
    @Delete("DELETE FROM product_specification WHERE product_id = #{productId}")
    int deleteByProductId(@Param("productId") Long productId);
} 