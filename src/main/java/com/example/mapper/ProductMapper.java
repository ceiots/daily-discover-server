package com.example.mapper;

import com.example.model.Product;
import com.example.model.Comment;
import org.apache.ibatis.annotations.*;
import com.example.util.*;
import java.util.List;

@Mapper
public interface ProductMapper {

    @Insert("INSERT INTO recommendations (title, imageUrl, shopName, price, soldCount, " +
            "shopAvatarUrl, specifications, product_details, purchase_notices, " +
            "storeDescription, created_at, category_id) " +
            "VALUES (#{title}, #{imageUrl}, #{shopName}, #{price}, #{soldCount}, " +
            "#{shopAvatarUrl}, #{specifications,typeHandler=com.example.util.SpecificationsTypeHandler}, " +
            "#{productDetails,typeHandler=com.example.util.ProductDetailsTypeHandler}, " +
            "#{purchaseNotices,typeHandler=com.example.util.PurchaseNoticesTypeHandler}, " +
            "#{storeDescription}, #{createdAt}, #{categoryId})")
    void insert(Product product);

    @Select("SELECT * FROM recommendations")
    @Results({
        @Result(property = "specifications", column = "specifications", 
                typeHandler = SpecificationsTypeHandler.class),
        @Result(property = "productDetails", column = "product_details", 
                typeHandler = ProductDetailsTypeHandler.class),
        @Result(property = "purchaseNotices", column = "purchase_notices", 
                typeHandler = PurchaseNoticesTypeHandler.class)
    })
    List<Product> getAllProducts();

    @Select("SELECT * FROM recommendations WHERE id = #{id}")
    @Results({
        @Result(property = "specifications", column = "specifications", 
                typeHandler = SpecificationsTypeHandler.class),
        @Result(property = "productDetails", column = "product_details", 
                typeHandler = ProductDetailsTypeHandler.class),
        @Result(property = "purchaseNotices", column = "purchase_notices", 
                typeHandler = PurchaseNoticesTypeHandler.class),
        @Result(property = "comments", column = "id", 
                many = @Many(select = "getCommentsByProductId")),
        @Result(property = "storeDescription", column = "storeDescription") // 确保映射
    })
    Product findById(Long id);

    @Select("SELECT * FROM recommendations WHERE category_id = #{categoryId}")
    @Results({
        @Result(property = "specifications", column = "specifications", 
                typeHandler = SpecificationsTypeHandler.class),
        @Result(property = "productDetails", column = "product_details", 
                typeHandler = ProductDetailsTypeHandler.class),
        @Result(property = "purchaseNotices", column = "purchase_notices", 
                typeHandler = PurchaseNoticesTypeHandler.class)
    })
    List<Product> findByCategoryId(Long categoryId);

    @Select("SELECT * FROM recommendations ORDER BY RAND() LIMIT 10")
    @Results({
        @Result(property = "specifications", column = "specifications", 
                typeHandler = SpecificationsTypeHandler.class),
        @Result(property = "productDetails", column = "product_details", 
                typeHandler = ProductDetailsTypeHandler.class),
        @Result(property = "purchaseNotices", column = "purchase_notices", 
                typeHandler = PurchaseNoticesTypeHandler.class)
    })
    List<Product> findRandom();

    @Select("SELECT * FROM recommendations WHERE title LIKE CONCAT('%', #{keyword}, '%')")
    @Results({
        @Result(property = "specifications", column = "specifications", 
                typeHandler = SpecificationsTypeHandler.class),
        @Result(property = "productDetails", column = "product_details", 
                typeHandler = ProductDetailsTypeHandler.class),
        @Result(property = "purchaseNotices", column = "purchase_notices", 
                typeHandler = PurchaseNoticesTypeHandler.class)
    })
    List<Product> searchProducts(String keyword);

    @Select("SELECT userName, userAvatarUrl, content, rating, date FROM comments WHERE recommendation_id = #{productId}")
    List<Comment> getCommentsByProductId(Long productId);
} 