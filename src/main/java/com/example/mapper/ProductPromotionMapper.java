package com.example.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.ProductPromotion;
import com.example.util.JsonTypeHandler;

import java.util.List;
import java.util.Date;

@Mapper
public interface ProductPromotionMapper extends BaseMapper<ProductPromotion> {

    @Insert("INSERT INTO product_promotion(product_id, sku_id, promotion_price, promotion_title, " +
            "promotion_desc, start_time, end_time, status, promotion_type, promotion_rule) " +
            "VALUES(#{productId}, #{skuId}, #{promotionPrice}, #{promotionTitle}, " +
            "#{promotionDesc}, #{startTime}, #{endTime}, #{status}, #{promotionType}, " +
            "#{promotionRule,typeHandler=com.example.util.JsonTypeHandler})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductPromotion promotion);
    
    @Select("SELECT * FROM product_promotion WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "skuId", column = "sku_id"),
        @Result(property = "promotionPrice", column = "promotion_price"),
        @Result(property = "promotionTitle", column = "promotion_title"),
        @Result(property = "promotionDesc", column = "promotion_desc"),
        @Result(property = "startTime", column = "start_time"),
        @Result(property = "endTime", column = "end_time"),
        @Result(property = "status", column = "status"),
        @Result(property = "promotionType", column = "promotion_type"),
        @Result(property = "promotionRule", column = "promotion_rule", 
                typeHandler = JsonTypeHandler.class),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    ProductPromotion findById(@Param("id") Long id);
    
    @Select("SELECT * FROM product_promotion WHERE product_id = #{productId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "skuId", column = "sku_id"),
        @Result(property = "promotionPrice", column = "promotion_price"),
        @Result(property = "promotionTitle", column = "promotion_title"),
        @Result(property = "promotionDesc", column = "promotion_desc"),
        @Result(property = "startTime", column = "start_time"),
        @Result(property = "endTime", column = "end_time"),
        @Result(property = "status", column = "status"),
        @Result(property = "promotionType", column = "promotion_type"),
        @Result(property = "promotionRule", column = "promotion_rule", 
                typeHandler = JsonTypeHandler.class),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<ProductPromotion> findByProductId(@Param("productId") Long productId);
    
    @Select("SELECT * FROM product_promotion WHERE sku_id = #{skuId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "skuId", column = "sku_id"),
        @Result(property = "promotionPrice", column = "promotion_price"),
        @Result(property = "promotionTitle", column = "promotion_title"),
        @Result(property = "promotionDesc", column = "promotion_desc"),
        @Result(property = "startTime", column = "start_time"),
        @Result(property = "endTime", column = "end_time"),
        @Result(property = "status", column = "status"),
        @Result(property = "promotionType", column = "promotion_type"),
        @Result(property = "promotionRule", column = "promotion_rule", 
                typeHandler = JsonTypeHandler.class),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<ProductPromotion> findBySkuId(@Param("skuId") Long skuId);
    
    @Select("SELECT * FROM product_promotion WHERE product_id = #{productId} AND " +
            "(sku_id = #{skuId} OR sku_id IS NULL) AND " +
            "status = 1 AND " +
            "(start_time IS NULL OR start_time <= NOW()) AND " +
            "(end_time IS NULL OR end_time >= NOW())")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "skuId", column = "sku_id"),
        @Result(property = "promotionPrice", column = "promotion_price"),
        @Result(property = "promotionTitle", column = "promotion_title"),
        @Result(property = "promotionDesc", column = "promotion_desc"),
        @Result(property = "startTime", column = "start_time"),
        @Result(property = "endTime", column = "end_time"),
        @Result(property = "status", column = "status"),
        @Result(property = "promotionType", column = "promotion_type"),
        @Result(property = "promotionRule", column = "promotion_rule", 
                typeHandler = JsonTypeHandler.class),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<ProductPromotion> findActivePromotions(@Param("productId") Long productId, @Param("skuId") Long skuId);
    
    @Update("UPDATE product_promotion SET " +
            "promotion_price = #{promotionPrice}, " +
            "promotion_title = #{promotionTitle}, " +
            "promotion_desc = #{promotionDesc}, " +
            "start_time = #{startTime}, " +
            "end_time = #{endTime}, " +
            "status = #{status}, " +
            "promotion_type = #{promotionType}, " +
            "promotion_rule = #{promotionRule,typeHandler=com.example.util.JsonTypeHandler}, " +
            "update_time = NOW() " +
            "WHERE id = #{id}")
    int update(ProductPromotion promotion);
    
    @Delete("DELETE FROM product_promotion WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    @Delete("DELETE FROM product_promotion WHERE product_id = #{productId}")
    int deleteByProductId(@Param("productId") Long productId);
    
    @Delete("DELETE FROM product_promotion WHERE sku_id = #{skuId}")
    int deleteBySkuId(@Param("skuId") Long skuId);
    
    @Update("UPDATE product_promotion SET status = #{status}, update_time = NOW() " +
            "WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
} 