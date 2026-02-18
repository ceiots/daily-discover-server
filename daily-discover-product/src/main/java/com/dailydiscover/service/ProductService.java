package com.dailydiscover.service;

import com.dailydiscover.dto.ProductDetailDTO;
import com.dailydiscover.model.Product;
import com.dailydiscover.model.ProductSku;
import com.dailydiscover.model.ProductSpec;
import com.dailydiscover.model.ProductRecommendation;
import java.util.List;
import java.util.Map;

public interface ProductService {
    // 基础CRUD操作
    Product findById(Long id);
    ProductDetailDTO getProductDetail(Long id);
    List<Product> findAll();
    List<Product> findBySellerId(Long sellerId);
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findHotProducts();
    List<Product> findNewProducts();
    List<Product> findRecommendedProducts();
    void save(Product product);
    void update(Product product);
    void delete(Long id);
    
    // SKU规格相关功能
    List<ProductSku> getProductSkus(Long productId);
    List<ProductSpec> getProductSpecs(Long productId);
    ProductSku getSkuById(Long skuId);
    Map<String, Object> getSkuSpecDetails(Long skuId);
    
    // 推荐系统功能
    List<ProductRecommendation> getProductRecommendations(Long productId, String recommendationType);
    List<Product> getPersonalizedRecommendations(Long userId);
    List<Product> getDailyDiscoverProducts();
    List<Product> getTrendingProducts();
    
    // 搜索功能
    List<Product> searchProducts(String keyword);
    List<String> getSearchSuggestions(String keyword);
    
    // 销量统计功能
    Map<String, Object> getProductSalesStats(Long productId, String timeGranularity);
    List<Product> getTopSellingProducts(int limit);
    
    // 用户行为记录
    void recordUserBehavior(Long userId, Long productId, String behaviorType, Map<String, Object> context);
}