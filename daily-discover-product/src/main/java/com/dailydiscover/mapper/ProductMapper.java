package com.dailydiscover.mapper;

import com.dailydiscover.model.Product;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductMapper {
    @Select("SELECT * FROM products WHERE id = #{id}")
    Product findById(Long id);
    
    @Select("SELECT * FROM products WHERE status = 'active' ORDER BY created_at DESC")
    List<Product> findAll();
    
    @Select("SELECT * FROM products WHERE seller_id = #{sellerId} AND status = 'active'")
    List<Product> findBySellerId(Long sellerId);
    
    @Select("SELECT * FROM products WHERE category_id = #{categoryId} AND status = 'active'")
    List<Product> findByCategoryId(Long categoryId);
    
    @Select("SELECT * FROM products WHERE is_hot = true AND status = 'active' ORDER BY total_sales DESC")
    List<Product> findHotProducts();
    
    @Select("SELECT * FROM products WHERE is_new = true AND status = 'active' ORDER BY created_at DESC")
    List<Product> findNewProducts();
    
    @Select("SELECT * FROM products WHERE is_recommended = true AND status = 'active' ORDER BY rating DESC")
    List<Product> findRecommendedProducts();
    
    @Insert("INSERT INTO products (seller_id, title, description, category_id, brand, base_price, original_price, discount, rating, review_count, total_sales, monthly_sales, is_new, is_hot, is_recommended, status, tags, main_image_url) " +
            "VALUES (#{sellerId}, #{title}, #{description}, #{categoryId}, #{brand}, #{basePrice}, #{originalPrice}, #{discount}, #{rating}, #{reviewCount}, #{totalSales}, #{monthlySales}, #{isNew}, #{isHot}, #{isRecommended}, #{status}, #{tags}, #{mainImageUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Product product);
    
    @Update("UPDATE products SET title = #{title}, description = #{description}, category_id = #{categoryId}, brand = #{brand}, base_price = #{basePrice}, original_price = #{originalPrice}, discount = #{discount}, rating = #{rating}, review_count = #{reviewCount}, total_sales = #{totalSales}, monthly_sales = #{monthlySales}, is_new = #{isNew}, is_hot = #{isHot}, is_recommended = #{isRecommended}, status = #{status}, tags = #{tags}, main_image_url = #{mainImageUrl}, updated_at = CURRENT_TIMESTAMP WHERE id = #{id}")
    void update(Product product);
    
    @Update("UPDATE products SET status = 'inactive' WHERE id = #{id}")
    void softDelete(Long id);
}