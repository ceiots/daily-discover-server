package com.dailydiscover.mapper;

import com.dailydiscover.model.ProductEntity;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DiscoverProductMapper {
    
    // 查找今日推荐商品
    @Select("SELECT p.* FROM products p " +
            "INNER JOIN daily_recommend_products drp ON p.id = drp.product_id " +
            "WHERE drp.recommend_date = CURDATE() AND p.is_active = true " +
            "ORDER BY drp.sort_order ASC")
    List<ProductEntity> findTodayProducts();
    
    // 查找昨日推荐商品
    @Select("SELECT p.* FROM products p " +
            "INNER JOIN daily_recommend_products drp ON p.id = drp.product_id " +
            "WHERE drp.recommend_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY) AND p.is_active = true " +
            "ORDER BY drp.sort_order ASC")
    List<ProductEntity> findYesterdayProducts();
    
    // 根据主题查找推荐商品
    @Select("SELECT p.* FROM products p " +
            "INNER JOIN daily_recommend_products drp ON p.id = drp.product_id " +
            "WHERE drp.theme_id = #{themeId} AND drp.recommend_date = #{date} AND p.is_active = true " +
            "ORDER BY drp.sort_order ASC")
    List<ProductEntity> findProductsByTheme(@Param("themeId") Long themeId, @Param("date") LocalDate date);
    
    // 查找所有活跃商品
    @Select("SELECT * FROM products WHERE is_active = true ORDER BY created_at DESC")
    List<ProductEntity> findAllActiveProducts();
    
    // 根据分类查找商品
    @Select("SELECT * FROM products WHERE category_id = #{categoryId} AND is_active = true")
    List<ProductEntity> findProductsByCategory(@Param("categoryId") Long categoryId);
    
    // 根据ID查找商品
    @Select("SELECT * FROM products WHERE id = #{id}")
    ProductEntity findById(@Param("id") Long id);
    
    // 插入商品
    @Insert("INSERT INTO products (name, description, price, image_url, category_id, is_active, view_count, like_count, favorite_count, created_at, updated_at) " +
            "VALUES (#{name}, #{description}, #{price}, #{imageUrl}, #{categoryId}, #{isActive}, #{viewCount}, #{likeCount}, #{favoriteCount}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductEntity product);
    
    // 更新商品
    @Update("UPDATE products SET name = #{name}, description = #{description}, price = #{price}, image_url = #{imageUrl}, " +
            "category_id = #{categoryId}, is_active = #{isActive}, view_count = #{viewCount}, like_count = #{likeCount}, " +
            "favorite_count = #{favoriteCount}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(ProductEntity product);
    
    // 删除商品
    @Delete("DELETE FROM products WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    // 增加浏览次数
    @Update("UPDATE products SET view_count = view_count + 1 WHERE id = #{id}")
    int incrementViewCount(@Param("id") Long id);
    
    // 增加点赞次数
    @Update("UPDATE products SET like_count = like_count + 1 WHERE id = #{id}")
    int incrementLikeCount(@Param("id") Long id);
    
    // 减少点赞次数
    @Update("UPDATE products SET like_count = GREATEST(0, like_count - 1) WHERE id = #{id}")
    int decrementLikeCount(@Param("id") Long id);
    
    // 增加收藏次数
    @Update("UPDATE products SET favorite_count = favorite_count + 1 WHERE id = #{id}")
    int incrementFavoriteCount(@Param("id") Long id);
    
    // 减少收藏次数
    @Update("UPDATE products SET favorite_count = GREATEST(0, favorite_count - 1) WHERE id = #{id}")
    int decrementFavoriteCount(@Param("id") Long id);
    
    // 批量插入商品
    @Insert("<script>" +
            "INSERT INTO products (name, description, price, image_url, category_id, is_active, view_count, like_count, favorite_count, created_at, updated_at) VALUES " +
            "<foreach collection='products' item='product' separator=','>" +
            "(#{product.name}, #{product.description}, #{product.price}, #{product.imageUrl}, #{product.categoryId}, #{product.isActive}, #{product.viewCount}, #{product.likeCount}, #{product.favoriteCount}, #{product.createdAt}, #{product.updatedAt})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("products") List<ProductEntity> products);
}