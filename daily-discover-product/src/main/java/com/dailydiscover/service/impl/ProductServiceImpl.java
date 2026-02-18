package com.dailydiscover.service.impl;

import com.dailydiscover.dto.ProductDetailDTO;
import com.dailydiscover.mapper.*;
import com.dailydiscover.model.*;
import com.dailydiscover.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.json.JSONObject;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private ProductSkuMapper productSkuMapper;
    
    @Autowired
    private ProductSpecMapper productSpecMapper;
    
    @Autowired
    private ProductRecommendationMapper productRecommendationMapper;
    
    @Autowired
    private ProductSalesStatsMapper productSalesStatsMapper;
    
    @Autowired
    private UserBehaviorMapper userBehaviorMapper;
    
    @Autowired
    private ProductSearchKeywordMapper productSearchKeywordMapper;
    
    @Override
    public Product findById(Long id) {
        try {
            Product product = productMapper.findById(id);
            return product;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取产品详情（包含属性信息）
     */
    public ProductDetailDTO getProductDetail(Long id) {
        Product product = productMapper.findById(id);
        if (product == null) {
            return null;
        }
        
        return new ProductDetailDTO(product, null);
    }
    
    @Override
    public List<Product> findAll() {
        return productMapper.findAll();
    }
    
    @Override
    public List<Product> findBySellerId(Long sellerId) {
        return productMapper.findBySellerId(sellerId);
    }
    
    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        return productMapper.findByCategoryId(categoryId);
    }
    
    @Override
    public List<Product> findHotProducts() {
        return productMapper.findHotProducts();
    }
    
    @Override
    public List<Product> findNewProducts() {
        return productMapper.findNewProducts();
    }
    
    @Override
    public List<Product> findRecommendedProducts() {
        return productMapper.findRecommendedProducts();
    }
    
    @Override
    @Transactional
    public void save(Product product) {
        productMapper.insert(product);
        // 由于表已拆分，需要创建ProductAttribute对象并保存
        // 这里需要根据业务逻辑创建ProductAttribute对象
        // 暂时保持原样，后续需要根据实际业务需求调整
    }
    
    @Override
    @Transactional
    public void update(Product product) {
        productMapper.update(product);
        // 由于表已拆分，需要同时更新ProductAttribute表
        // 暂时保持原样，后续需要根据实际业务需求调整
    }
    
    @Override
    public void delete(Long id) {
        productMapper.softDelete(id);
    }
    
    // SKU规格相关功能实现
    @Override
    public List<ProductSku> getProductSkus(Long productId) {
        return productSkuMapper.findByProductId(productId);
    }
    
    @Override
    public List<ProductSpec> getProductSpecs(Long productId) {
        return productSpecMapper.findByProductId(productId);
    }
    
    @Override
    public ProductSku getSkuById(Long skuId) {
        return productSkuMapper.findById(skuId);
    }
    
    @Override
    public Map<String, Object> getSkuSpecDetails(Long skuId) {
        ProductSku sku = productSkuMapper.findById(skuId);
        if (sku == null) {
            return Collections.emptyMap();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("sku", sku);
        result.put("specs", productSpecMapper.findByProductId(sku.getProductId()));
        return result;
    }
    
    // 推荐系统功能实现
    @Override
    public List<ProductRecommendation> getProductRecommendations(Long productId, String recommendationType) {
        return productRecommendationMapper.findByProductIdAndType(productId, recommendationType);
    }
    
    @Override
    public List<Product> getPersonalizedRecommendations(Long userId) {
        List<ProductRecommendation> recommendations = productRecommendationMapper.findByUserId(userId);
        return recommendations.stream()
                .map(rec -> productMapper.findById(rec.getRecommendedProductId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> getDailyDiscoverProducts() {
        return productRecommendationMapper.findDailyDiscoverProducts()
                .stream()
                .map(rec -> productMapper.findById(rec.getRecommendedProductId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Product> getTrendingProducts() {
        return productSalesStatsMapper.findTrendingProducts()
                .stream()
                .map(stats -> productMapper.findById(stats.getProductId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    // 搜索功能实现
    @Override
    public List<Product> searchProducts(String keyword) {
        // 记录搜索关键词
        productSearchKeywordMapper.recordSearch(keyword);
        return productMapper.searchByKeyword(keyword);
    }
    
    @Override
    public List<String> getSearchSuggestions(String keyword) {
        return productSearchKeywordMapper.findSuggestions(keyword);
    }
    
    // 销量统计功能实现
    @Override
    public Map<String, Object> getProductSalesStats(Long productId, String timeGranularity) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("daily", productSalesStatsMapper.findByProductIdAndGranularity(productId, "daily"));
        stats.put("monthly", productSalesStatsMapper.findByProductIdAndGranularity(productId, "monthly"));
        stats.put("yearly", productSalesStatsMapper.findByProductIdAndGranularity(productId, "yearly"));
        return stats;
    }
    
    @Override
    public List<Product> getTopSellingProducts(int limit) {
        return productSalesStatsMapper.findTopSellingProducts(limit)
                .stream()
                .map(stats -> productMapper.findById(stats.getProductId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    // 用户行为记录实现
    @Override
    @Transactional
    public void recordUserBehavior(Long userId, Long productId, String behaviorType, Map<String, Object> context) {
        UserBehaviorLog behaviorLog = new UserBehaviorLog();
        behaviorLog.setUserId(userId);
        behaviorLog.setProductId(productId);
        behaviorLog.setBehaviorType(behaviorType);
        behaviorLog.setBehaviorContext(context != null ? new JSONObject(context).toString() : null);
        behaviorLog.setCreatedAt(new Date());
        
        userBehaviorMapper.insert(behaviorLog);
    }
}