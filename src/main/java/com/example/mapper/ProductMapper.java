package com.example.mapper;

import com.example.model.Product;
import com.example.model.Comment;
import com.example.model.Shop;
import org.apache.ibatis.annotations.*;
import com.example.util.*;
import java.util.List;

@Mapper
public interface ProductMapper {

        @Insert("INSERT INTO product (title, imageUrl, price, soldCount, stock, " + "specifications, product_details, purchase_notices, " +
            "created_at, category_id, parent_category_id, grand_category_id, shop_id, user_id, audit_status, audit_remark, " +
            "product_sku_id, total_stock) " +
            "VALUES (#{title}, #{imageUrl}, #{price}, #{soldCount}, #{stock}, " +
            "#{specifications,typeHandler=com.example.util.SpecificationsTypeHandler}, " +
            "#{productDetails,typeHandler=com.example.util.ProductDetailsTypeHandler}, " +
            "#{purchaseNotices,typeHandler=com.example.util.PurchaseNoticesTypeHandler}, " +
            "#{createdAt}, #{categoryId}, #{parentCategoryId}, #{grandCategoryId}, #{shopId}, #{userId}, #{auditStatus}, #{auditRemark}, " +
            "#{productSkuId}, #{totalStock})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Product product);

    @Select("SELECT r.*, s.shop_name, s.shop_logo, s.shop_description " +
            "FROM product r " +
            "LEFT JOIN shop s ON r.shop_id = s.id " +
            "WHERE r.deleted = 0 AND r.audit_status = 1 " +
            "LIMIT #{limit} OFFSET #{offset}")
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
        @Result(property = "auditRemark", column = "audit_remark"),
        @Result(property = "productSkuId", column = "product_sku_id"),
        @Result(property = "totalStock", column = "total_stock")
    })
    List<Product> getProductsWithPagination(@Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT r.*, s.shop_name, s.shop_logo, s.shop_description " +
            "FROM product r " +
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
                one = @One(select = "com.example.mapper.ShopMapper.findById")),
        @Result(property = "productSkuId", column = "product_sku_id"),
        @Result(property = "totalStock", column = "total_stock")
    })
    Product findById(Long id);

    @Select("SELECT r.*, s.shop_name, s.shop_logo, s.shop_description " +
            "FROM product r " +
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
                one = @One(select = "com.example.mapper.ShopMapper.findById")),
        @Result(property = "productSkuId", column = "product_sku_id"),
        @Result(property = "totalStock", column = "total_stock")
    })
    List<Product> findByCategoryId(Long categoryId);

    @Select("SELECT r.*, s.shop_name, s.shop_logo, s.shop_description " +
            "FROM product r " +
            "LEFT JOIN shop s ON r.shop_id = s.id " +
            "WHERE r.deleted = 0 AND r.audit_status = 1 " +
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
                one = @One(select = "com.example.mapper.ShopMapper.findById")),
        @Result(property = "productSkuId", column = "product_sku_id"),
        @Result(property = "totalStock", column = "total_stock")
    })
    List<Product> findRandom();

    /**
     * 获取指定数量的随机商品
     * @param limit 要返回的商品数量
     * @return 随机商品列表
     */
    @Select("SELECT r.*, s.shop_name, s.shop_logo, s.shop_description " +
            "FROM product r " +
            "LEFT JOIN shop s ON r.shop_id = s.id " +
            "WHERE r.deleted = 0 AND r.audit_status = 1 " +
            "ORDER BY RAND() LIMIT #{limit}")
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
        @Result(property = "productSkuId", column = "product_sku_id"),
        @Result(property = "totalStock", column = "total_stock")
    })
    List<Product> findRandomWithLimit(@Param("limit") int limit);

    @Select("SELECT r.*, s.shop_name, s.shop_logo, s.shop_description " +
            "FROM product r " +
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
                one = @One(select = "com.example.mapper.ShopMapper.findById")),
        @Result(property = "productSkuId", column = "product_sku_id"),
        @Result(property = "totalStock", column = "total_stock")
    })
    List<Product> searchProducts(String keyword);

    @Select("SELECT user_name, user_avatar_url, content, rating, date FROM comments WHERE product_id = #{productId}")
    List<Comment> getCommentsByProductId(Long productId);
    
    @Select("SELECT r.*, s.shop_name, s.shop_logo, s.shop_description " +
            "FROM product r " +
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
                one = @One(select = "com.example.mapper.ShopMapper.findById")),
        @Result(property = "productSkuId", column = "product_sku_id"),
        @Result(property = "totalStock", column = "total_stock")
    })
    List<Product> findByShopId(Long shopId);
    
    /**
     * 根据店铺ID获取已审核通过的商品列表
     */
    @Select("SELECT r.*, s.shop_name, s.shop_logo, s.shop_description " +
            "FROM product r " +
            "LEFT JOIN shop s ON r.shop_id = s.id " +
            "WHERE r.shop_id = #{shopId} AND r.deleted = 0 AND r.audit_status = 1")
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
        @Result(property = "productSkuId", column = "product_sku_id"),
        @Result(property = "totalStock", column = "total_stock")
    })
    List<Product> findApprovedByShopId(Long shopId);
    
    /**
     * 更新商品
     */
    @Update("UPDATE product SET " +
            "title = #{title}, " +
            "price = #{price}, " +
            "stock = #{stock}, " +
            "total_stock = #{totalStock}, " +
            "product_sku_id = #{productSkuId}, " +
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
    @Update("UPDATE product SET deleted = 1 WHERE id = #{id}")
    void deleteById(Long id);

    /**
     * 添加审核接口
     */
    @Update("UPDATE product SET audit_status = #{auditStatus}, audit_remark = #{auditRemark} WHERE id = #{id}")
    void updateAuditStatus(@Param("id") Long id, @Param("auditStatus") Integer auditStatus, @Param("auditRemark") String auditRemark);

    /**
     * 获取待审核的商品列表（管理员使用）
     */
    @Select("SELECT r.*, s.shop_name, s.shop_logo, s.shop_description " +
            "FROM product r " +
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
        @Result(property = "auditRemark", column = "audit_remark"),
        @Result(property = "productSkuId", column = "product_sku_id"),
        @Result(property = "totalStock", column = "total_stock")
    })
    List<Product> findPendingAuditProducts();

    @Select("SELECT COUNT(*) FROM product r WHERE r.deleted = 0 AND r.audit_status = 1")
    int countApprovedProducts();
    
    /**
     * 统计所有商品数量（包括待审核的）
     */
    @Select("SELECT COUNT(*) FROM product r WHERE r.deleted = 0")
    int countProducts();
    
    @Select("SELECT r.*, s.shop_name, s.shop_logo, s.shop_description " +
            "FROM product r " +
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
        @Result(property = "auditRemark", column = "audit_remark"),
        @Result(property = "productSkuId", column = "product_sku_id"),
        @Result(property = "totalStock", column = "total_stock")
    })
    List<Product> getAllProducts();
}