package com.example.mapper;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.model.ProductReview;
import com.example.util.JsonTypeHandler;

import java.util.List;

@Mapper
public interface ProductReviewMapper extends BaseMapper<ProductReview> {

    @Insert("INSERT INTO product_review(product_id, order_id, user_id, sku_id, rating, content, " +
            "images, is_anonymous, status, has_image) " +
            "VALUES(#{productId}, #{orderId}, #{userId}, #{skuId}, #{rating}, #{content}, " +
            "#{images,typeHandler=com.example.util.JsonTypeHandler}, #{isAnonymous}, #{status}, #{hasImage})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductReview review);
    
    @Select("SELECT * FROM product_review WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "skuId", column = "sku_id"),
        @Result(property = "rating", column = "rating"),
        @Result(property = "content", column = "content"),
        @Result(property = "images", column = "images", typeHandler = JsonTypeHandler.class),
        @Result(property = "isAnonymous", column = "is_anonymous"),
        @Result(property = "replyContent", column = "reply_content"),
        @Result(property = "replyTime", column = "reply_time"),
        @Result(property = "status", column = "status"),
        @Result(property = "hasImage", column = "has_image"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    ProductReview findById(@Param("id") Long id);
    
    @Select("<script>" +
            "SELECT r.*, u.username as userName, u.avatar_url as userAvatar, " +
            "p.title as productTitle, p.image_url as productImage, " +
            "ps.specifications as skuSpecifications " +
            "FROM product_review r " +
            "LEFT JOIN user u ON r.user_id = u.id " +
            "LEFT JOIN product p ON r.product_id = p.id " +
            "LEFT JOIN product_sku ps ON r.sku_id = ps.id " +
            "WHERE r.product_id = #{productId} " +
            "<if test='hasImage != null'> AND r.has_image = #{hasImage} </if> " +
            "<if test='rating != null'> AND r.rating = #{rating} </if> " +
            "ORDER BY r.create_time DESC" +
            "</script>")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "skuId", column = "sku_id"),
        @Result(property = "rating", column = "rating"),
        @Result(property = "content", column = "content"),
        @Result(property = "images", column = "images", typeHandler = JsonTypeHandler.class),
        @Result(property = "isAnonymous", column = "is_anonymous"),
        @Result(property = "replyContent", column = "reply_content"),
        @Result(property = "replyTime", column = "reply_time"),
        @Result(property = "status", column = "status"),
        @Result(property = "hasImage", column = "has_image"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "userName", column = "userName"),
        @Result(property = "userAvatar", column = "userAvatar"),
        @Result(property = "productTitle", column = "productTitle"),
        @Result(property = "productImage", column = "productImage"),
        @Result(property = "skuSpecifications", column = "skuSpecifications")
    })
    Page<ProductReview> findByProductId(Page<ProductReview> page, 
                                        @Param("productId") Long productId, 
                                        @Param("hasImage") Boolean hasImage,
                                        @Param("rating") Integer rating);
    
    @Select("SELECT * FROM product_review WHERE user_id = #{userId} ORDER BY create_time DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "skuId", column = "sku_id"),
        @Result(property = "rating", column = "rating"),
        @Result(property = "content", column = "content"),
        @Result(property = "images", column = "images", typeHandler = JsonTypeHandler.class),
        @Result(property = "isAnonymous", column = "is_anonymous"),
        @Result(property = "replyContent", column = "reply_content"),
        @Result(property = "replyTime", column = "reply_time"),
        @Result(property = "status", column = "status"),
        @Result(property = "hasImage", column = "has_image"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<ProductReview> findByUserId(@Param("userId") Long userId);
    
    @Update("UPDATE product_review SET " +
            "reply_content = #{replyContent}, " +
            "reply_time = NOW(), " +
            "update_time = NOW() " +
            "WHERE id = #{id}")
    int updateReply(@Param("id") Long id, @Param("replyContent") String replyContent);
    
    @Update("UPDATE product_review SET " +
            "status = #{status}, " +
            "update_time = NOW() " +
            "WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    @Delete("DELETE FROM product_review WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    @Select("SELECT COUNT(*) FROM product_review WHERE product_id = #{productId}")
    int countByProductId(@Param("productId") Long productId);
    
    @Select("SELECT COUNT(*) FROM product_review WHERE product_id = #{productId} AND rating >= 4")
    int countPositiveByProductId(@Param("productId") Long productId);
    
    @Select("SELECT COUNT(*) FROM product_review WHERE product_id = #{productId} AND rating <= 2")
    int countNegativeByProductId(@Param("productId") Long productId);
    
    @Select("SELECT AVG(rating) FROM product_review WHERE product_id = #{productId}")
    double getAverageRating(@Param("productId") Long productId);
    
    @Select("SELECT COUNT(*) FROM product_review WHERE product_id = #{productId} AND rating = #{rating}")
    int countByRating(@Param("productId") Long productId, @Param("rating") Integer rating);
    
    @Select("SELECT COUNT(*) FROM product_review WHERE product_id = #{productId} AND has_image = 1")
    int countWithImage(@Param("productId") Long productId);

    @Select("<script>" +
            "SELECT r.*, u.username as userName, u.avatar_url as userAvatar, " +
            "p.title as productTitle, p.image_url as productImage, " +
            "ps.specifications as skuSpecifications " +
            "FROM product_review r " +
            "LEFT JOIN user u ON r.user_id = u.id " +
            "LEFT JOIN product p ON r.product_id = p.id " +
            "LEFT JOIN product_sku ps ON r.sku_id = ps.id " +
            "WHERE r.product_id = #{productId} " +
            "<if test='hasImage != null'> AND r.has_image = #{hasImage} </if> " +
            "<if test='rating != null'> AND r.rating = #{rating} </if> " +
            "ORDER BY r.create_time DESC" +
            "</script>")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "skuId", column = "sku_id"),
        @Result(property = "rating", column = "rating"),
        @Result(property = "content", column = "content"),
        @Result(property = "images", column = "images", typeHandler = JsonTypeHandler.class),
        @Result(property = "isAnonymous", column = "is_anonymous"),
        @Result(property = "replyContent", column = "reply_content"),
        @Result(property = "replyTime", column = "reply_time"),
        @Result(property = "status", column = "status"),
        @Result(property = "hasImage", column = "has_image"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "userName", column = "userName"),
        @Result(property = "userAvatar", column = "userAvatar"),
        @Result(property = "productTitle", column = "productTitle"),
        @Result(property = "productImage", column = "productImage"),
        @Result(property = "skuSpecifications", column = "skuSpecifications")
    })
    List<ProductReview> findByProductIdSimple(@Param("productId") Long productId, 
                                           @Param("hasImage") Boolean hasImage,
                                           @Param("rating") Integer rating);

    @Select("SELECT * FROM product_review WHERE product_id = #{productId} ORDER BY create_time DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "skuId", column = "sku_id"),
        @Result(property = "rating", column = "rating"),
        @Result(property = "content", column = "content"),
        @Result(property = "images", column = "images", typeHandler = JsonTypeHandler.class),
        @Result(property = "isAnonymous", column = "is_anonymous"),
        @Result(property = "replyContent", column = "reply_content"),
        @Result(property = "replyTime", column = "reply_time"),
        @Result(property = "status", column = "status"),
        @Result(property = "hasImage", column = "has_image"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<ProductReview> findByProductId(@Param("productId") Long productId);
} 