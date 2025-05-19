package com.example.mapper;

import com.example.model.Product;
import com.example.model.Comment;
import com.example.model.Shop;
import org.apache.ibatis.annotations.*;
import com.example.util.*;
import java.util.List;

@Mapper
public interface ProductMapper {

        @Insert("INSERT INTO recommendations (title, imageUrl, price, soldCount, stock, " + "specifications, product_details, purchase_notices, " +
            "created_at, category_id, parent_category_id, grand_category_id, shop_id, user_id, audit_status, audit_remark) " +
            "VALUES (#{title}, #{imageUrl}, #{price}, #{soldCount}, #{stock}, " +
            "#{specifications,typeHandler=com.example.util.SpecificationsTypeHandler}, " +
            "#{productDetails,typeHandler=com.example.util.ProductDetailsTypeHandler}, " +
            "#{purchaseNotices,typeHandler=com.example.util.PurchaseNoticesTypeHandler}, " +
            "#{createdAt}, #{categoryId}, #{parentCategoryId}, #{grandCategoryId}, #{shopId}, #{userId}, #{auditStatus}, #{auditRemark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Product product);

    @Select("SELECT r.*, s.shop_name, s.shop_logo, s.shop_description " +
            "FROM recommendations r " +
            "LEFT JOIN shop s ON r.shop_id = s.id " +
            "WHERE r.deleted = 0 AND r.audit_status = 1")
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
                one = @One(select = "com.example.mapper.ShopMapper.findById")),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "parentCategoryId", column = "parent_category_id"),
        @Result(property = "grandCategoryId", column = "grand_category_id"),
        @Result(property = "auditStatus", column = "audit_status"),
        @Result(property = "auditRemark", column = "audit_remark")
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
    
    /**
     * 根据店铺ID获取商品列表
     */
    @Select("SELECT r.*, s.shop_name, s.shop_logo, s.shop_description " +
            "FROM recommendations r " +
            "LEFT JOIN shop s ON r.shop_id = s.id " +
            "WHERE r.shop_id = #{shopId}")
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
    List<Product> findByShopId(Long shopId);
    
    /**
     * 更新商品
     */
    @Update("UPDATE recommendations SET " +
            "title = #{title}, " +
            "price = #{price}, " +
            "stock = #{stock}, " +
            "category_id = #{categoryId}, " +
            "parent_category_id = #{parentCategoryId}, " +
            "grand_category_id = #{grandCategoryId}, " +
            "specifications = #{specifications,typeHandler=com.example.util.SpecificationsTypeHandler}, " +
            "product_details = #{productDetails,typeHandler=com.example.util.ProductDetailsTypeHandler}, " +
            "purchase_notices = #{purchaseNotices,typeHandler=com.example.util.PurchaseNoticesTypeHandler}, " +
            "audit_status = #{auditStatus}, " +
            "audit_remark = #{auditRemark} " +
            "WHERE id = #{id}")
    void update(Product product);
    
    /**
     * 设置商品为已删除状态（逻辑删除）
     */
    @Update("UPDATE recommendations SET deleted = 1 WHERE id = #{id}")
    void deleteById(Long id);

    /**
     * 添加审核接口
     */
    @Update("UPDATE recommendations SET audit_status = #{auditStatus}, audit_remark = #{auditRemark} WHERE id = #{id}")
    void updateAuditStatus(@Param("id") Long id, @Param("auditStatus") Integer auditStatus, @Param("auditRemark") String auditRemark);

    /**
     * 获取待审核的商品列表（管理员使用）
     */
    @Select("SELECT r.*, s.shop_name, s.shop_logo, s.shop_description " +
            "FROM recommendations r " +
            "LEFT JOIN shop s ON r.shop_id = s.id " +
            "WHERE r.deleted = 0 AND r.audit_status = 0")
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
                one = @One(select = "com.example.mapper.ShopMapper.findById")),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "parentCategoryId", column = "parent_category_id"),
        @Result(property = "grandCategoryId", column = "grand_category_id"),
        @Result(property = "auditStatus", column = "audit_status"),
        @Result(property = "auditRemark", column = "audit_remark")
    })
    List<Product> findPendingAuditProducts();
}