package com.dailydiscover.service;

import com.dailydiscover.mapper.DiscoverProductMapper;
import com.dailydiscover.model.ProductEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;

@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private DiscoverProductMapper discoverProductMapper;
    
    @Override
    public List<ProductEntity> getAllActiveProducts() {
        return discoverProductMapper.findAllActiveProducts();
    }
    
    @Override
    public ProductEntity getProductById(Long id) {
        return discoverProductMapper.findById(id);
    }
    
    @Override
    public List<ProductEntity> getProductsByTimeSlot(String timeSlot) {
        return discoverProductMapper.findByTimeSlot(timeSlot);
    }
    
    @Override
    public List<ProductEntity> getProductsByCategory(String category) {
        return discoverProductMapper.findByCategory(category);
    }
    
    @Override
    public List<ProductEntity> searchProducts(String keyword) {
        return discoverProductMapper.searchProducts(keyword);
    }
    
    @Override
    public ProductEntity createProduct(ProductEntity product) {
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        // 如果没有设置isActive，默认为true
        if (product.getIsActive() == null) {
            product.setIsActive(true);
        }
        discoverProductMapper.insert(product);
        return product;
    }
    
    @Override
    public ProductEntity updateProduct(Long id, ProductEntity productDetails) {
        ProductEntity product = getProductById(id);
        if (product == null) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        
        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());
        product.setDescription(productDetails.getDescription());
        product.setImageUrl(productDetails.getImageUrl());
        product.setCategory(productDetails.getCategory());
        product.setIsActive(productDetails.getIsActive());
        product.setUpdatedAt(LocalDateTime.now());
        
        discoverProductMapper.update(product);
        return product;
    }
    
    @Override
    public void deleteProduct(Long id) {
        ProductEntity product = getProductById(id);
        if (product == null) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        product.setIsActive(false);
        product.setUpdatedAt(LocalDateTime.now());
        discoverProductMapper.update(product);
    }
    
    @Override
    public List<ProductEntity> getLatestProducts() {
        return discoverProductMapper.findLatestProducts();
    }
    
    @Override
    public Map<String, Object> getRecommendations() {
        Map<String, Object> result = new HashMap<>();
        
        // 获取推荐商品
        List<ProductEntity> recommendedProducts = getProductsByTimeSlot(getCurrentTimeSlot());
        result.put("recommendedProducts", recommendedProducts);
        
        return result;
    }
    
    @Override
    public ProductEntity getProductDetail(Long id) {
        return getProductById(id);
    }
    
    @Override
    public Map<String, Object> getProductComments(Long id, int page, int size) {
        // 简化实现，返回空评论列表
        Map<String, Object> result = new HashMap<>();
        result.put("comments", new ArrayList<>());
        result.put("total", 0);
        return result;
    }
    
    @Override
    public List<ProductEntity> getRelatedProducts(Long id, Integer categoryId) {
        if (categoryId != null) {
            return discoverProductMapper.findByCategory(categoryId.toString());
        }
        return new ArrayList<>();
    }
    
    @Override
    public Map<String, Object> checkProductStock(Long id, Map<String, String> specs) {
        // 简化实现，返回有库存
        Map<String, Object> result = new HashMap<>();
        result.put("inStock", true);
        result.put("quantity", 100);
        return result;
    }
    
    @Override
    public Map<String, Object> getSpecPrice(Long id, Map<String, String> specs) {
        // 简化实现，返回原价
        Map<String, Object> result = new HashMap<>();
        ProductEntity product = getProductById(id);
        result.put("price", product != null ? product.getPrice() : BigDecimal.ZERO);
        return result;
    }
    
    // 从DiscoverDataService整合的功能
    @Override
    public List<ProductEntity> getTodayProducts() {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        return discoverProductMapper.findTodayProducts(today);
    }
    
    @Override
    public List<ProductEntity> getYesterdayProducts() {
        String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE);
        return discoverProductMapper.findYesterdayProducts(yesterday);
    }
    
    @Override
    public List<ProductEntity> getProductsByTheme(Long themeId) {
        return discoverProductMapper.findByTheme(themeId);
    }
    
    @Override
    public void initializeSampleProducts() {
        // 创建示例商品数据
        List<ProductEntity> sampleProducts = new ArrayList<>();
        
        ProductEntity product1 = new ProductEntity();
        product1.setName("精选咖啡豆");
        product1.setPrice(new BigDecimal("128.0"));
        product1.setDescription("来自哥伦比亚的优质阿拉比卡咖啡豆，口感醇厚，香气浓郁");
        product1.setImageUrl("https://via.placeholder.com/300x200/8B4513/FFFFFF?text=咖啡豆");
        product1.setCategory("饮品");
        product1.setIsActive(true);
        sampleProducts.add(product1);
        
        ProductEntity product2 = new ProductEntity();
        product2.setName("手工陶瓷杯");
        product2.setPrice(new BigDecimal("88.0"));
        product2.setDescription("手工制作的陶瓷杯，独特的设计，适合日常使用");
        product2.setImageUrl("https://via.placeholder.com/300x200/DEB887/FFFFFF?text=陶瓷杯");
        product2.setCategory("生活用品");
        product2.setIsActive(true);
        sampleProducts.add(product2);
        
        ProductEntity product3 = new ProductEntity();
        product3.setName("天然精油");
        product3.setPrice(new BigDecimal("158.0"));
        product3.setDescription("纯天然植物精油，有助于放松身心，改善睡眠质量");
        product3.setImageUrl("https://via.placeholder.com/300x200/9370DB/FFFFFF?text=精油");
        product3.setCategory("个护健康");
        product3.setIsActive(true);
        sampleProducts.add(product3);
        
        // 批量插入示例数据
        for (ProductEntity product : sampleProducts) {
            createProduct(product);
        }
    }
    
    // 获取当前时间段
    private String getCurrentTimeSlot() {
        int hour = java.time.LocalTime.now().getHour();
        if (hour >= 6 && hour < 12) {
            return "morning";
        } else if (hour >= 12 && hour < 14) {
            return "noon";
        } else if (hour >= 14 && hour < 18) {
            return "afternoon";
        } else {
            return "evening";
        }
    }
}