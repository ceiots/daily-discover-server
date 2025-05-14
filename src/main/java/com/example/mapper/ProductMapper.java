package com.example.mapper;

import com.example.model.Product;
import com.example.model.Comment;
import com.example.model.Shop;
import org.apache.ibatis.annotations.*;
import com.example.util.*;
import java.util.List;

@Mapper
public interface ProductMapper {

    @Insert("INSERT INTO recommendations (title, imageUrl, shopName, price, soldCount, " +
            "shopAvatarUrl, specifications, product_details, purchase_notices, " +
            "created_at, category_id, shop_id, user_id) " +
            "VALUES (#{title}, #{imageUrl}, #{shopName}, #{price}, #{soldCount}, " +
            "#{shopAvatarUrl}, #{specifications,typeHandler=com.example.util.SpecificationsTypeHandler}, " +
            "#{productDetails,typeHandler=com.example.util.ProductDetailsTypeHandler}, " +
            "#{purchaseNotices,typeHandler=com.example.util.PurchaseNoticesTypeHandler}, " +
            "#{createdAt}, #{categoryId}, #{shopId}, #{userId})")
    void insert(Product product);

    @Select("SELECT r.*, s.shop_name, s.shop_logo, s.shop_description " +
            "FROM recommendations r " +
            "LEFT JOIN shop s ON r.shop_id = s.id")
    @Results({
        @Result(property = "specifications", column = "specifications", 
                typeHandler = SpecificationsTypeHandler.class),
        @Result(property = "productDetails", column = "product_details", 
                typeHandler = ProductDetailsTypeHandler.class),
        @Result(property = "purchaseNotices", column = "purchase_notices", 
                typeHandler = PurchaseNoticesTypeHandler.class),
        @Result(property = "shopName", column = "shop_name"),
        @Result(property = "shopAvatarUrl", column = "shop_logo"),
        @Result(property = "storeDescription", column = "shop_description"),
        @Result(property = "shop", column = "shop_id", 
                one = @One(select = "com.example.mapper.ShopMapper.findById"))
    })
    List<Product> getAllProducts();

    @Select("SELECT r.*, s.shop_name, s.shop_logo, s.shop_description " +
            "FROM recommendations r " +
            "LEFT JOIN shop s ON r.shop_id = s.id " +
            "WHERE r.id = #{id}")
    @Results({
        @Result(property = "specifications", column = "specifications", 
                typeHandler = SpecificationsTypeHandler.class),
        @Result(property = "productDetails", column = "product_details", 
                typeHandler = ProductDetailsTypeHandler.class),
        @Result(property = "purchaseNotices", column = "purchase_notices", 
                typeHandler = PurchaseNoticesTypeHandler.class),
        @Result(property = "comments", column = "id", 
                many = @Many(select = "getCommentsByProductId")),
        @Result(property = "storeDescription", column = "shop_description"),
        @Result(property = "shopName", column = "shop_name"),
        @Result(property = "shopAvatarUrl", column = "shop_logo"),
        @Result(property = "shop", column = "shop_id", 
                one = @One(select = "com.example.mapper.ShopMapper.findById"))
    })
    Product findById(Long id);

    @Select("SELECT r.*, s.shop_name, s.shop_logo, s.shop_description " +
            "FROM recommendations r " +
            "LEFT JOIN shop s ON r.shop_id = s.id " +
            "WHERE r.category_id = #{categoryId}")
    @Results({
        @Result(property = "specifications", column = "specifications", 
                typeHandler = SpecificationsTypeHandler.class),
        @Result(property = "productDetails", column = "product_details", 
                typeHandler = ProductDetailsTypeHandler.class),
        @Result(property = "purchaseNotices", column = "purchase_notices", 
                typeHandler = PurchaseNoticesTypeHandler.class),
        @Result(property = "shopName", column = "shop_name"),
        @Result(property = "shopAvatarUrl", column = "shop_logo"),
        @Result(property = "storeDescription", column = "shop_description"),
        @Result(property = "shop", column = "shop_id", 
                one = @One(select = "com.example.mapper.ShopMapper.findById"))
    })
    List<Product> findByCategoryId(Long categoryId);

    @Select("SELECT r.*, s.shop_name, s.shop_logo, s.shop_description " +
            "FROM recommendations r " +
            "LEFT JOIN shop s ON r.shop_id = s.id " +
            "ORDER BY RAND() LIMIT 10")
    @Results({
        @Result(property = "specifications", column = "specifications", 
                typeHandler = SpecificationsTypeHandler.class),
        @Result(property = "productDetails", column = "product_details", 
                typeHandler = ProductDetailsTypeHandler.class),
        @Result(property = "purchaseNotices", column = "purchase_notices", 
                typeHandler = PurchaseNoticesTypeHandler.class),
        @Result(property = "shopName", column = "shop_name"),
        @Result(property = "shopAvatarUrl", column = "shop_logo"),
        @Result(property = "storeDescription", column = "shop_description"),
        @Result(property = "shop", column = "shop_id", 
                one = @One(select = "com.example.mapper.ShopMapper.findById"))
    })
    List<Product> findRandom();

    @Select("SELECT r.*, s.shop_name, s.shop_logo, s.shop_description " +
            "FROM recommendations r " +
            "LEFT JOIN shop s ON r.shop_id = s.id " +
            "WHERE r.title LIKE CONCAT('%', #{keyword}, '%')")
    @Results({
        @Result(property = "specifications", column = "specifications", 
                typeHandler = SpecificationsTypeHandler.class),
        @Result(property = "productDetails", column = "product_details", 
                typeHandler = ProductDetailsTypeHandler.class),
        @Result(property = "purchaseNotices", column = "purchase_notices", 
                typeHandler = PurchaseNoticesTypeHandler.class),
        @Result(property = "shopName", column = "shop_name"),
        @Result(property = "shopAvatarUrl", column = "shop_logo"),
        @Result(property = "storeDescription", column = "shop_description"),
        @Result(property = "shop", column = "shop_id", 
                one = @One(select = "com.example.mapper.ShopMapper.findById"))
    })
    List<Product> searchProducts(String keyword);

    @Select("SELECT userName, userAvatarUrl, content, rating, date FROM comments WHERE recommendation_id = #{productId}")
    List<Comment> getCommentsByProductId(Long productId);
    
    @Select("SELECT r.*, s.shop_name, s.shop_logo, s.shop_description " +
            "FROM recommendations r " +
            "LEFT JOIN shop s ON r.shop_id = s.id " +
            "WHERE r.user_id = #{userId}")
    @Results({
        @Result(property = "specifications", column = "specifications", 
                typeHandler = SpecificationsTypeHandler.class),
        @Result(property = "productDetails", column = "product_details", 
                typeHandler = ProductDetailsTypeHandler.class),
        @Result(property = "purchaseNotices", column = "purchase_notices", 
                typeHandler = PurchaseNoticesTypeHandler.class),
        @Result(property = "shopName", column = "shop_name"),
        @Result(property = "shopAvatarUrl", column = "shop_logo"),
        @Result(property = "storeDescription", column = "shop_description"),
        @Result(property = "shop", column = "shop_id", 
                one = @One(select = "com.example.mapper.ShopMapper.findById"))
    })
    List<Product> findByUserId(Long userId);
}