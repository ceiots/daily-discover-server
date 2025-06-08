package com.example.mapper;

import com.example.model.Product;
import com.example.model.Comment;
import com.example.model.Shop;
import org.apache.ibatis.annotations.*;
import com.example.util.*;
import java.util.List;

@Mapper
public interface ProductMapper {

        @Insert("INSERT INTO product (title, description, image_url, category_id, shop_id, brand_id, " + 
            "status, audit_status, price, original_price, total_stock, total_sales, weight, " +
            "create_time, update_time, deleted) " +
            "VALUES (#{title}, #{description}, #{imageUrl}, #{categoryId}, #{shopId}, #{brandId}, " +
            "#{status}, #{auditStatus}, #{price}, #{originalPrice}, #{totalStock}, #{totalSales}, #{weight}, " +
            "#{createTime}, #{updateTime}, #{deleted})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Product product);

    @Select("SELECT * FROM product WHERE deleted = 0 AND audit_status = 1 " +
            "LIMIT #{limit} OFFSET #{offset}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "title", column = "title"),
        @Result(property = "description", column = "description"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "shopId", column = "shop_id"),
        @Result(property = "brandId", column = "brand_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "auditStatus", column = "audit_status"),
        @Result(property = "price", column = "price"),
        @Result(property = "originalPrice", column = "original_price"),
        @Result(property = "totalStock", column = "total_stock"),
        @Result(property = "totalSales", column = "total_sales"),
        @Result(property = "weight", column = "weight"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "deleted", column = "deleted"),
        @Result(property = "shop", column = "shop_id", 
                one = @One(select = "com.example.mapper.ShopMapper.findById")),
        @Result(property = "skus", column = "id", 
                many = @Many(select = "com.example.mapper.ProductSkuMapper.findByProductId")),
        @Result(property = "categoryRelations", column = "id", 
                many = @Many(select = "com.example.mapper.ProductCategoryRelationMapper.findByProductId"))
    })
    List<Product> getProductsWithPagination(@Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT * FROM product WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "title", column = "title"),
        @Result(property = "description", column = "description"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "shopId", column = "shop_id"),
        @Result(property = "brandId", column = "brand_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "auditStatus", column = "audit_status"),
        @Result(property = "price", column = "price"),
        @Result(property = "originalPrice", column = "original_price"),
        @Result(property = "totalStock", column = "total_stock"),
        @Result(property = "totalSales", column = "total_sales"),
        @Result(property = "weight", column = "weight"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "deleted", column = "deleted"),
        @Result(property = "shop", column = "shop_id", 
                one = @One(select = "com.example.mapper.ShopMapper.findById")),
        @Result(property = "skus", column = "id", 
                many = @Many(select = "com.example.mapper.ProductSkuMapper.findByProductId")),
        @Result(property = "categoryRelations", column = "id", 
                many = @Many(select = "com.example.mapper.ProductCategoryRelationMapper.findByProductId"))
    })
    Product findById(Long id);

    @Select("SELECT * FROM product WHERE category_id = #{categoryId} AND deleted = 0")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "title", column = "title"),
        @Result(property = "description", column = "description"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "shopId", column = "shop_id"),
        @Result(property = "brandId", column = "brand_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "auditStatus", column = "audit_status"),
        @Result(property = "price", column = "price"),
        @Result(property = "originalPrice", column = "original_price"),
        @Result(property = "totalStock", column = "total_stock"),
        @Result(property = "totalSales", column = "total_sales"),
        @Result(property = "weight", column = "weight"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "deleted", column = "deleted"),
        @Result(property = "shop", column = "shop_id", 
                one = @One(select = "com.example.mapper.ShopMapper.findById")),
        @Result(property = "skus", column = "id", 
                many = @Many(select = "com.example.mapper.ProductSkuMapper.findByProductId"))
    })
    List<Product> findByCategoryId(Long categoryId);

    @Select("SELECT * FROM product WHERE deleted = 0 AND audit_status = 1 " +
            "ORDER BY RAND() LIMIT 10")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "title", column = "title"),
        @Result(property = "description", column = "description"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "shopId", column = "shop_id"),
        @Result(property = "brandId", column = "brand_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "auditStatus", column = "audit_status"),
        @Result(property = "price", column = "price"),
        @Result(property = "originalPrice", column = "original_price"),
        @Result(property = "totalStock", column = "total_stock"),
        @Result(property = "totalSales", column = "total_sales"),
        @Result(property = "weight", column = "weight"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "deleted", column = "deleted"),
        @Result(property = "shop", column = "shop_id", 
                one = @One(select = "com.example.mapper.ShopMapper.findById")),
        @Result(property = "skus", column = "id", 
                many = @Many(select = "com.example.mapper.ProductSkuMapper.findByProductId"))
    })
    List<Product> findRandom();

    /**
     * 获取指定数量的随机商品
     * @param limit 要返回的商品数量
     * @return 随机商品列表
     */
    @Select("SELECT * FROM product WHERE deleted = 0 AND audit_status = 1 " +
            "ORDER BY RAND() LIMIT #{limit}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "title", column = "title"),
        @Result(property = "description", column = "description"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "shopId", column = "shop_id"),
        @Result(property = "brandId", column = "brand_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "auditStatus", column = "audit_status"),
        @Result(property = "price", column = "price"),
        @Result(property = "originalPrice", column = "original_price"),
        @Result(property = "totalStock", column = "total_stock"),
        @Result(property = "totalSales", column = "total_sales"),
        @Result(property = "weight", column = "weight"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "deleted", column = "deleted"),
        @Result(property = "shop", column = "shop_id", 
                one = @One(select = "com.example.mapper.ShopMapper.findById")),
        @Result(property = "skus", column = "id", 
                many = @Many(select = "com.example.mapper.ProductSkuMapper.findByProductId"))
    })
    List<Product> findRandomWithLimit(@Param("limit") int limit);

    @Select("SELECT * FROM product WHERE title LIKE CONCAT('%', #{keyword}, '%') AND deleted = 0")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "title", column = "title"),
        @Result(property = "description", column = "description"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "shopId", column = "shop_id"),
        @Result(property = "brandId", column = "brand_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "auditStatus", column = "audit_status"),
        @Result(property = "price", column = "price"),
        @Result(property = "originalPrice", column = "original_price"),
        @Result(property = "totalStock", column = "total_stock"),
        @Result(property = "totalSales", column = "total_sales"),
        @Result(property = "weight", column = "weight"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "deleted", column = "deleted"),
        @Result(property = "shop", column = "shop_id", 
                one = @One(select = "com.example.mapper.ShopMapper.findById")),
        @Result(property = "skus", column = "id", 
                many = @Many(select = "com.example.mapper.ProductSkuMapper.findByProductId"))
    })
    List<Product> searchProducts(String keyword);

    @Select("SELECT user_name, user_avatar_url, content, rating, date FROM comments WHERE product_id = #{productId}")
    List<Comment> getCommentsByProductId(Long productId);
    
    @Select("SELECT * FROM product WHERE shop_id = #{shopId} AND deleted = 0")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "title", column = "title"),
        @Result(property = "description", column = "description"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "shopId", column = "shop_id"),
        @Result(property = "brandId", column = "brand_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "auditStatus", column = "audit_status"),
        @Result(property = "price", column = "price"),
        @Result(property = "originalPrice", column = "original_price"),
        @Result(property = "totalStock", column = "total_stock"),
        @Result(property = "totalSales", column = "total_sales"),
        @Result(property = "weight", column = "weight"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "deleted", column = "deleted"),
        @Result(property = "shop", column = "shop_id", 
                one = @One(select = "com.example.mapper.ShopMapper.findById")),
        @Result(property = "skus", column = "id", 
                many = @Many(select = "com.example.mapper.ProductSkuMapper.findByProductId"))
    })
    List<Product> findByShopId(Long shopId);
    
    /**
     * 根据店铺ID获取已审核通过的商品列表
     */
    @Select("SELECT * FROM product WHERE shop_id = #{shopId} AND deleted = 0 AND audit_status = 1")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "title", column = "title"),
        @Result(property = "description", column = "description"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "shopId", column = "shop_id"),
        @Result(property = "brandId", column = "brand_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "auditStatus", column = "audit_status"),
        @Result(property = "price", column = "price"),
        @Result(property = "originalPrice", column = "original_price"),
        @Result(property = "totalStock", column = "total_stock"),
        @Result(property = "totalSales", column = "total_sales"),
        @Result(property = "weight", column = "weight"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "deleted", column = "deleted"),
        @Result(property = "shop", column = "shop_id", 
                one = @One(select = "com.example.mapper.ShopMapper.findById")),
        @Result(property = "skus", column = "id", 
                many = @Many(select = "com.example.mapper.ProductSkuMapper.findByProductId"))
    })
    List<Product> findApprovedByShopId(Long shopId);
    
    /**
     * 更新商品
     */
    @Update("UPDATE product SET " +
            "title = #{title}, " +
            "description = #{description}, " +
            "image_url = #{imageUrl}, " +
            "category_id = #{categoryId}, " +
            "shop_id = #{shopId}, " +
            "brand_id = #{brandId}, " +
            "status = #{status}, " +
            "audit_status = #{auditStatus}, " +
            "price = #{price}, " +
            "original_price = #{originalPrice}, " +
            "total_stock = #{totalStock}, " +
            "total_sales = #{totalSales}, " +
            "weight = #{weight}, " +
            "update_time = #{updateTime}, " +
            "deleted = #{deleted} " +
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
    @Update("UPDATE product SET audit_status = #{auditStatus}, update_time = NOW() WHERE id = #{id}")
    void updateAuditStatus(@Param("id") Long id, @Param("auditStatus") Integer auditStatus, @Param("auditRemark") String auditRemark);

    /**
     * 获取待审核的商品列表（管理员使用）
     */
    @Select("SELECT * FROM product WHERE deleted = 0 AND audit_status = 0")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "title", column = "title"),
        @Result(property = "description", column = "description"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "shopId", column = "shop_id"),
        @Result(property = "brandId", column = "brand_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "auditStatus", column = "audit_status"),
        @Result(property = "price", column = "price"),
        @Result(property = "originalPrice", column = "original_price"),
        @Result(property = "totalStock", column = "total_stock"),
        @Result(property = "totalSales", column = "total_sales"),
        @Result(property = "weight", column = "weight"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "deleted", column = "deleted"),
        @Result(property = "shop", column = "shop_id", 
                one = @One(select = "com.example.mapper.ShopMapper.findById")),
        @Result(property = "skus", column = "id", 
                many = @Many(select = "com.example.mapper.ProductSkuMapper.findByProductId"))
    })
    List<Product> findPendingAuditProducts();

    @Select("SELECT COUNT(*) FROM product WHERE deleted = 0 AND audit_status = 1")
    int countApprovedProducts();
    
    /**
     * 统计所有商品数量（包括待审核的）
     */
    @Select("SELECT COUNT(*) FROM product WHERE deleted = 0")
    int countProducts();
    
    @Select("SELECT * FROM product WHERE deleted = 0 AND audit_status = 1")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "title", column = "title"),
        @Result(property = "description", column = "description"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "shopId", column = "shop_id"),
        @Result(property = "brandId", column = "brand_id"),
        @Result(property = "status", column = "status"),
        @Result(property = "auditStatus", column = "audit_status"),
        @Result(property = "price", column = "price"),
        @Result(property = "originalPrice", column = "original_price"),
        @Result(property = "totalStock", column = "total_stock"),
        @Result(property = "totalSales", column = "total_sales"),
        @Result(property = "weight", column = "weight"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time"),
        @Result(property = "deleted", column = "deleted"),
        @Result(property = "shop", column = "shop_id", 
                one = @One(select = "com.example.mapper.ShopMapper.findById")),
        @Result(property = "skus", column = "id", 
                many = @Many(select = "com.example.mapper.ProductSkuMapper.findByProductId"))
    })
    List<Product> getAllProducts();

    @Select("SELECT COUNT(*) FROM product WHERE deleted = 0 AND audit_status = #{auditStatus}")
    int countByAuditStatus(@Param("auditStatus") Integer auditStatus);
    
    /**
     * 根据类别ID和审核状态获取商品，按创建时间倒序排序
     * 用于推荐系统
     * @param categoryId 类别ID
     * @param auditStatus 审核状态
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 商品列表
     */
    @Select("SELECT * FROM product WHERE category_id = #{categoryId} AND audit_status = #{auditStatus} " +
            "AND deleted = 0 ORDER BY create_time DESC LIMIT #{limit} OFFSET #{offset}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "title", column = "title"),
        @Result(property = "description", column = "description"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "shopId", column = "shop_id"),
        @Result(property = "price", column = "price"),
        @Result(property = "originalPrice", column = "original_price"),
        @Result(property = "totalStock", column = "total_stock"),
        @Result(property = "totalSales", column = "total_sales")
    })
    List<Product> findByCategoryIdAndAuditStatusOrderByCreatedAtDesc(
            @Param("categoryId") Long categoryId, 
            @Param("auditStatus") Integer auditStatus, 
            @Param("offset") Integer offset, 
            @Param("limit") Integer limit);
    
    /**
     * 根据审核状态获取商品，按销量倒序排序
     * 用于推荐系统
     * @param auditStatus 审核状态
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 商品列表
     */
    @Select("SELECT * FROM product WHERE audit_status = #{auditStatus} AND deleted = 0 " +
            "ORDER BY total_sales DESC LIMIT #{limit} OFFSET #{offset}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "title", column = "title"),
        @Result(property = "description", column = "description"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "shopId", column = "shop_id"),
        @Result(property = "price", column = "price"),
        @Result(property = "originalPrice", column = "original_price"),
        @Result(property = "totalStock", column = "total_stock"),
        @Result(property = "totalSales", column = "total_sales")
    })
    List<Product> findByAuditStatusOrderBySoldCountDesc(
            @Param("auditStatus") Integer auditStatus, 
            @Param("offset") Integer offset, 
            @Param("limit") Integer limit);
    
    /**
     * 根据审核状态获取商品，按创建时间倒序排序
     * 用于推荐系统
     * @param auditStatus 审核状态
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 商品列表
     */
    @Select("SELECT * FROM product WHERE audit_status = #{auditStatus} AND deleted = 0 " +
            "ORDER BY create_time DESC LIMIT #{limit} OFFSET #{offset}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "title", column = "title"),
        @Result(property = "description", column = "description"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "shopId", column = "shop_id"),
        @Result(property = "price", column = "price"),
        @Result(property = "originalPrice", column = "original_price"),
        @Result(property = "totalStock", column = "total_stock"),
        @Result(property = "totalSales", column = "total_sales")
    })
    List<Product> findByAuditStatusOrderByCreatedAtDesc(
            @Param("auditStatus") Integer auditStatus, 
            @Param("offset") Integer offset, 
            @Param("limit") Integer limit);
}