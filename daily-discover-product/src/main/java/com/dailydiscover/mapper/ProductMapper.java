package com.dailydiscover.mapper;

import com.dailydiscover.model.Product;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductMapper {
    
    // 查找所有商品
    @Select("SELECT * FROM products")
    List<Product> findAll();
    
    // 根据id查找商品
    @Select("SELECT * FROM products WHERE id = #{id}")
    Product findById(Long id);
    
    // 查找所有活跃商品
    @Select("SELECT * FROM products WHERE is_active = true")
    List<Product> findAllByIsActiveTrue();
    
    // 根据时间段查找商品
    @Select("SELECT * FROM products WHERE time_slot = #{timeSlot} AND is_active = true")
    List<Product> findByTimeSlotAndIsActiveTrue(@Param("timeSlot") String timeSlot);
    
    // 根据分类查找商品
    @Select("SELECT * FROM products WHERE category = #{category} AND is_active = true")
    List<Product> findByCategoryAndIsActiveTrue(@Param("category") String category);
    
    // 搜索商品（按标题或描述）
    @Select("SELECT * FROM products WHERE is_active = true AND (title LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))")
    List<Product> searchActiveProducts(@Param("keyword") String keyword);
    
    // 按创建时间降序查找活跃商品
    @Select("SELECT * FROM products WHERE is_active = true ORDER BY created_at DESC")
    List<Product> findByIsActiveTrueOrderByCreatedAtDesc();
    
    // 插入商品
    @Insert("INSERT INTO products (title, price, description, image_url, time_slot, category, original_price, tag, reason, is_hot_sale, is_high_quality, is_fast_delivery, features, is_active, created_at, updated_at) " +
            "VALUES (#{title}, #{price}, #{description}, #{imageUrl}, #{timeSlot}, #{category}, #{originalPrice}, #{tag}, #{reason}, #{isHotSale}, #{isHighQuality}, #{isFastDelivery}, #{features}, #{isActive}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Product product);
    
    // 更新商品
    @Update("UPDATE products SET title = #{title}, price = #{price}, description = #{description}, image_url = #{imageUrl}, " +
            "time_slot = #{timeSlot}, category = #{category}, original_price = #{originalPrice}, tag = #{tag}, " +
            "reason = #{reason}, is_hot_sale = #{isHotSale}, is_high_quality = #{isHighQuality}, is_fast_delivery = #{isFastDelivery}, " +
            "features = #{features}, is_active = #{isActive}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(Product product);
    
    // 删除商品
    @Delete("DELETE FROM products WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    // 查询具有特定特性的商品（热销、高质量或快速配送）
    List<Product> findFeaturedProducts();
}