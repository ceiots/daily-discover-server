package com.example.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.ProductContent;
import com.example.util.JsonTypeHandler;

@Mapper
public interface ProductContentMapper extends BaseMapper<ProductContent> {

    @Insert("INSERT INTO product_content(product_id, images, details, purchase_notices, video_url, " +
            "rich_description, seo_title, seo_keywords, seo_description) " +
            "VALUES(#{productId}, #{images,typeHandler=com.example.util.JsonTypeHandler}, " +
            "#{details,typeHandler=com.example.util.JsonTypeHandler}, " +
            "#{purchaseNotices,typeHandler=com.example.util.JsonTypeHandler}, " +
            "#{videoUrl}, #{richDescription}, #{seoTitle}, #{seoKeywords}, #{seoDescription})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductContent content);
    
    @Select("SELECT * FROM product_content WHERE product_id = #{productId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "images", column = "images", 
                typeHandler = JsonTypeHandler.class),
        @Result(property = "details", column = "details", 
                typeHandler = JsonTypeHandler.class),
        @Result(property = "purchaseNotices", column = "purchase_notices", 
                typeHandler = JsonTypeHandler.class),
        @Result(property = "videoUrl", column = "video_url"),
        @Result(property = "richDescription", column = "rich_description"),
        @Result(property = "seoTitle", column = "seo_title"),
        @Result(property = "seoKeywords", column = "seo_keywords"),
        @Result(property = "seoDescription", column = "seo_description"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    ProductContent findByProductId(@Param("productId") Long productId);
    
    @Update("UPDATE product_content SET " +
            "images = #{images,typeHandler=com.example.util.JsonTypeHandler}, " +
            "details = #{details,typeHandler=com.example.util.JsonTypeHandler}, " +
            "purchase_notices = #{purchaseNotices,typeHandler=com.example.util.JsonTypeHandler}, " +
            "video_url = #{videoUrl}, " +
            "rich_description = #{richDescription}, " +
            "seo_title = #{seoTitle}, " +
            "seo_keywords = #{seoKeywords}, " +
            "seo_description = #{seoDescription}, " +
            "update_time = NOW() " +
            "WHERE product_id = #{productId}")
    int updateByProductId(ProductContent content);
    
    @Delete("DELETE FROM product_content WHERE product_id = #{productId}")
    int deleteByProductId(@Param("productId") Long productId);
} 