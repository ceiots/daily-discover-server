package com.dailydiscover.mapper;

import com.dailydiscover.model.Product;
import com.dailydiscover.model.ProductDetail;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

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
    
    // ProductService 需要的方法
    @Select("SELECT * FROM products WHERE is_active = true")
    List<Product> findAllActiveProducts();
    
    @Select("SELECT * FROM products WHERE id = #{id}")
    Product findProductById(Long id);
    
    @Select("SELECT * FROM products WHERE time_slot = #{timeSlot} AND is_active = true")
    List<Product> findProductsByTimeSlot(@Param("timeSlot") String timeSlot);
    
    @Select("SELECT * FROM products WHERE category = #{category} AND is_active = true")
    List<Product> findProductsByCategory(@Param("category") String category);
    
    @Select("SELECT * FROM products WHERE is_active = true AND (title LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);
    
    @Select("SELECT * FROM products WHERE is_active = true ORDER BY created_at DESC LIMIT 10")
    List<Product> findLatestProducts();
    
    // 生活美学文章（模拟数据）
    @Select("SELECT 'article' as type, id, title, description as content, image_url as imageUrl FROM products WHERE category = 'lifestyle' AND is_active = true LIMIT 5")
    List<Map<String, Object>> findLifestyleArticles();
    
    // 热点话题（模拟数据）
    @Select("SELECT 'topic' as type, id, title, description as content FROM products WHERE is_hot_sale = true AND is_active = true LIMIT 5")
    List<Map<String, Object>> findHotTopics();
    
    // 产品详情
    @Select("SELECT p.*, p.description as detailDescription FROM products p WHERE p.id = #{id} AND p.is_active = true")
    ProductDetail findProductDetailById(Long id);
    
    // 产品评论（模拟数据）
    @Select("SELECT 'comment' as type, 'user' as username, 'Great product!' as content, NOW() as createdAt FROM products WHERE id = #{id} LIMIT #{size} OFFSET #{offset}")
    Map<String, Object> findProductComments(@Param("id") Long id, @Param("offset") int offset, @Param("size") int size);
    
    // 相关产品
    @Select("SELECT * FROM products WHERE category = (SELECT category FROM products WHERE id = #{id}) AND id != #{id} AND is_active = true LIMIT 5")
    List<Product> findRelatedProducts(@Param("id") Long id, @Param("categoryId") Integer categoryId);
    
    // 检查库存（模拟数据）
    @Select("SELECT 100 as stock, true as available")
    Map<String, Object> checkProductStock(@Param("id") Long id, @Param("specs") Map<String, String> specs);
    
    // 获取规格价格（模拟数据）
    @Select("SELECT price FROM products WHERE id = #{id}")
    Map<String, Object> getSpecPrice(@Param("id") Long id, @Param("specs") Map<String, String> specs);
    
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
