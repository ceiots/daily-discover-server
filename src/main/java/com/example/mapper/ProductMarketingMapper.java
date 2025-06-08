package com.example.mapper;

import org.apache.ibatis.annotations.*;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.ProductMarketing;

import java.util.List;

@Mapper
public interface ProductMarketingMapper extends BaseMapper<ProductMarketing> {

    @Insert("INSERT INTO product_marketing(product_id, is_hot, is_new, is_recommended, " +
            "labels, related_product_ids, show_in_homepage, marketing_start_time, " +
            "marketing_end_time, sort_weight) " +
            "VALUES(#{productId}, #{isHot}, #{isNew}, #{isRecommended}, " +
            "#{labels,typeHandler=com.example.util.JsonTypeHandler}, " +
            "#{relatedProductIds,typeHandler=com.example.util.JsonTypeHandler}, " +
            "#{showInHomepage}, #{marketingStartTime}, #{marketingEndTime}, #{sortWeight})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductMarketing marketing);
    
    @Select("SELECT * FROM product_marketing WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "isHot", column = "is_hot"),
        @Result(property = "isNew", column = "is_new"),
        @Result(property = "isRecommended", column = "is_recommended"),
        @Result(property = "labels", column = "labels", 
                typeHandler = com.example.util.JsonTypeHandler.class),
        @Result(property = "relatedProductIds", column = "related_product_ids", 
                typeHandler = com.example.util.JsonTypeHandler.class),
        @Result(property = "showInHomepage", column = "show_in_homepage"),
        @Result(property = "marketingStartTime", column = "marketing_start_time"),
        @Result(property = "marketingEndTime", column = "marketing_end_time"),
        @Result(property = "sortWeight", column = "sort_weight"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    ProductMarketing findById(@Param("id") Long id);
    
    @Select("SELECT * FROM product_marketing WHERE product_id = #{productId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "isHot", column = "is_hot"),
        @Result(property = "isNew", column = "is_new"),
        @Result(property = "isRecommended", column = "is_recommended"),
        @Result(property = "labels", column = "labels", 
                typeHandler = com.example.util.JsonTypeHandler.class),
        @Result(property = "relatedProductIds", column = "related_product_ids", 
                typeHandler = com.example.util.JsonTypeHandler.class),
        @Result(property = "showInHomepage", column = "show_in_homepage"),
        @Result(property = "marketingStartTime", column = "marketing_start_time"),
        @Result(property = "marketingEndTime", column = "marketing_end_time"),
        @Result(property = "sortWeight", column = "sort_weight"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    ProductMarketing findByProductId(@Param("productId") Long productId);
    
    @Update("UPDATE product_marketing SET " +
            "is_hot = #{isHot}, " +
            "is_new = #{isNew}, " +
            "is_recommended = #{isRecommended}, " +
            "labels = #{labels,typeHandler=com.example.util.JsonTypeHandler}, " +
            "related_product_ids = #{relatedProductIds,typeHandler=com.example.util.JsonTypeHandler}, " +
            "show_in_homepage = #{showInHomepage}, " +
            "marketing_start_time = #{marketingStartTime}, " +
            "marketing_end_time = #{marketingEndTime}, " +
            "sort_weight = #{sortWeight} " +
            "WHERE product_id = #{productId}")
    int updateByProductId(ProductMarketing marketing);
    
    @Select("SELECT * FROM product_marketing WHERE is_hot = 1 AND " +
            "(marketing_start_time IS NULL OR marketing_start_time <= NOW()) AND " +
            "(marketing_end_time IS NULL OR marketing_end_time >= NOW()) " +
            "ORDER BY sort_weight DESC LIMIT #{limit}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "isHot", column = "is_hot"),
        @Result(property = "isNew", column = "is_new"),
        @Result(property = "isRecommended", column = "is_recommended"),
        @Result(property = "labels", column = "labels", 
                typeHandler = com.example.util.JsonTypeHandler.class),
        @Result(property = "relatedProductIds", column = "related_product_ids", 
                typeHandler = com.example.util.JsonTypeHandler.class),
        @Result(property = "showInHomepage", column = "show_in_homepage"),
        @Result(property = "marketingStartTime", column = "marketing_start_time"),
        @Result(property = "marketingEndTime", column = "marketing_end_time"),
        @Result(property = "sortWeight", column = "sort_weight"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<ProductMarketing> findHotProducts(@Param("limit") int limit);
    
    @Select("SELECT * FROM product_marketing WHERE is_new = 1 AND " +
            "(marketing_start_time IS NULL OR marketing_start_time <= NOW()) AND " +
            "(marketing_end_time IS NULL OR marketing_end_time >= NOW()) " +
            "ORDER BY sort_weight DESC LIMIT #{limit}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "isHot", column = "is_hot"),
        @Result(property = "isNew", column = "is_new"),
        @Result(property = "isRecommended", column = "is_recommended"),
        @Result(property = "labels", column = "labels", 
                typeHandler = com.example.util.JsonTypeHandler.class),
        @Result(property = "relatedProductIds", column = "related_product_ids", 
                typeHandler = com.example.util.JsonTypeHandler.class),
        @Result(property = "showInHomepage", column = "show_in_homepage"),
        @Result(property = "marketingStartTime", column = "marketing_start_time"),
        @Result(property = "marketingEndTime", column = "marketing_end_time"),
        @Result(property = "sortWeight", column = "sort_weight"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<ProductMarketing> findNewProducts(@Param("limit") int limit);
    
    @Select("SELECT * FROM product_marketing WHERE is_recommended = 1 AND " +
            "(marketing_start_time IS NULL OR marketing_start_time <= NOW()) AND " +
            "(marketing_end_time IS NULL OR marketing_end_time >= NOW()) " +
            "ORDER BY sort_weight DESC LIMIT #{limit}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "isHot", column = "is_hot"),
        @Result(property = "isNew", column = "is_new"),
        @Result(property = "isRecommended", column = "is_recommended"),
        @Result(property = "labels", column = "labels", 
                typeHandler = com.example.util.JsonTypeHandler.class),
        @Result(property = "relatedProductIds", column = "related_product_ids", 
                typeHandler = com.example.util.JsonTypeHandler.class),
        @Result(property = "showInHomepage", column = "show_in_homepage"),
        @Result(property = "marketingStartTime", column = "marketing_start_time"),
        @Result(property = "marketingEndTime", column = "marketing_end_time"),
        @Result(property = "sortWeight", column = "sort_weight"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<ProductMarketing> findRecommendedProducts(@Param("limit") int limit);
    
    @Select("SELECT * FROM product_marketing WHERE show_in_homepage = 1 AND " +
            "(marketing_start_time IS NULL OR marketing_start_time <= NOW()) AND " +
            "(marketing_end_time IS NULL OR marketing_end_time >= NOW()) " +
            "ORDER BY sort_weight DESC LIMIT #{limit}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "isHot", column = "is_hot"),
        @Result(property = "isNew", column = "is_new"),
        @Result(property = "isRecommended", column = "is_recommended"),
        @Result(property = "labels", column = "labels", 
                typeHandler = com.example.util.JsonTypeHandler.class),
        @Result(property = "relatedProductIds", column = "related_product_ids", 
                typeHandler = com.example.util.JsonTypeHandler.class),
        @Result(property = "showInHomepage", column = "show_in_homepage"),
        @Result(property = "marketingStartTime", column = "marketing_start_time"),
        @Result(property = "marketingEndTime", column = "marketing_end_time"),
        @Result(property = "sortWeight", column = "sort_weight"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<ProductMarketing> findHomepageProducts(@Param("limit") int limit);
} 