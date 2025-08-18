package com.dailydiscover.mapper;

import com.dailydiscover.model.ProductEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ProductMapper {
    
    // 查找今日推荐商品
    List<ProductEntity> findTodayProducts(@Param("date") String date);
    
    // 查找昨日推荐商品
    List<ProductEntity> findYesterdayProducts(@Param("date") String date);
    
    // 根据主题查找推荐商品
    List<ProductEntity> findProductsByTheme(@Param("themeId") Long themeId, @Param("date") String date);
    
    // 查找所有活跃商品
    List<ProductEntity> findAllActiveProducts();
    
    // 根据分类查找商品
    List<ProductEntity> findProductsByCategory(@Param("categoryId") Long categoryId);
    
    // 根据ID查找商品
    ProductEntity findById(@Param("id") Long id);
    
    // 插入商品
    int insert(ProductEntity product);
    
    // 更新商品
    int update(ProductEntity product);
    
    // 删除商品
    int delete(@Param("id") Long id);
    
    // 更新商品浏览次数
    int updateViewCount(@Param("id") Long id);
    
    // 更新商品点赞次数
    int updateLikeCount(@Param("id") Long id);
    
    // 更新商品收藏次数
    int updateFavoriteCount(@Param("id") Long id);
    
    // 批量插入商品
    int batchInsert(@Param("products") List<ProductEntity> products);
    
    // 根据时间段查找商品
    List<ProductEntity> findByTimeSlot(@Param("timeSlot") String timeSlot);
    
    // 根据分类名称查找商品
    List<ProductEntity> findByCategory(@Param("category") String category);
    
    // 搜索商品
    List<ProductEntity> searchProducts(@Param("keyword") String keyword);
    
    // 查找最新商品
    List<ProductEntity> findLatestProducts();
    
    // 根据主题查找商品
    List<ProductEntity> findByTheme(@Param("themeId") Long themeId);
}