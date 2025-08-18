package com.dailydiscover.service;

import com.dailydiscover.model.ProductEntity;
import java.util.List;
import java.util.Map;

public interface ProductService {
    
    // 获取所有活跃商品
    List<ProductEntity> getAllActiveProducts();
    
    // 根据ID获取商品
    ProductEntity getProductById(Long id);
    
    // 根据时间段获取推荐商品
    List<ProductEntity> getProductsByTimeSlot(String timeSlot);
    
    // 根据分类获取商品
    List<ProductEntity> getProductsByCategory(String category);
    
    // 搜索商品
    List<ProductEntity> searchProducts(String keyword);
    
    // 创建新商品
    ProductEntity createProduct(ProductEntity product);
    
    // 更新商品
    ProductEntity updateProduct(Long id, ProductEntity productDetails);
    
    // 删除商品（软删除）
    void deleteProduct(Long id);
    
    // 获取最新商品
    List<ProductEntity> getLatestProducts();
    
    // 获取推荐数据
    Map<String, Object> getRecommendations();
    
    // 获取产品详情
    ProductEntity getProductDetail(Long id);
    
    // 获取产品评论
    Map<String, Object> getProductComments(Long id, int page, int size);
    
    // 获取相关产品
    List<ProductEntity> getRelatedProducts(Long id, Integer categoryId);
    
    // 检查产品库存
    Map<String, Object> checkProductStock(Long id, Map<String, String> specs);
    
    // 获取规格价格
    Map<String, Object> getSpecPrice(Long id, Map<String, String> specs);
    
    // 从DiscoverDataService整合的功能
    // 获取今日商品
    List<ProductEntity> getTodayProducts();
    
    // 获取昨日商品
    List<ProductEntity> getYesterdayProducts();
    
    // 根据主题获取商品
    List<ProductEntity> getProductsByTheme(Long themeId);
    
    // 初始化示例商品数据
    void initializeSampleProducts();
}