package com.dailydiscover.service;

import com.dailydiscover.mapper.ProductMapper;
import com.dailydiscover.model.Product;
import com.dailydiscover.model.ProductDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    // 获取所有活跃商品
    public List<Product> getAllActiveProducts() {
        return productMapper.findAllActiveProducts();
    }
    
    // 根据ID获取商品
    public Product getProductById(Long id) {
        return productMapper.findProductById(id);
    }
    
    // 根据时间段获取推荐商品
    public List<Product> getProductsByTimeSlot(String timeSlot) {
        return productMapper.findProductsByTimeSlot(timeSlot);
    }
    
    // 根据分类获取商品
    public List<Product> getProductsByCategory(String category) {
        return productMapper.findProductsByCategory(category);
    }
    
    // 搜索商品
    public List<Product> searchProducts(String keyword) {
        return productMapper.searchProducts(keyword);
    }
    
    // 创建新商品
    public Product createProduct(Product product) {
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        // 如果没有设置isActive，默认为true
        if (product.getIsActive() == null) {
            product.setIsActive(true);
        }
        return product;
    }
    
    // 更新商品
    public Product updateProduct(Long id, Product productDetails) {
        Product product = getProductById(id);
        if (product == null) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        
        product.setTitle(productDetails.getTitle());
        product.setPrice(productDetails.getPrice());
        product.setDescription(productDetails.getDescription());
        product.setImageUrl(productDetails.getImageUrl());
        product.setTimeSlot(productDetails.getTimeSlot());
        product.setCategory(productDetails.getCategory());
        product.setOriginalPrice(productDetails.getOriginalPrice());
        product.setTag(productDetails.getTag());
        product.setReason(productDetails.getReason());
        product.setIsActive(productDetails.getIsActive());
        product.setUpdatedAt(LocalDateTime.now());
        
        return product;
    }
    
    // 删除商品（软删除）
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        if (product == null) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        product.setIsActive(false);
        product.setUpdatedAt(LocalDateTime.now());
    }
    
    // 获取最新商品
    public List<Product> getLatestProducts() {
        return productMapper.findLatestProducts();
    }
    
    // 获取推荐数据（包含推荐商品、生活美学文章、热点话题）
    public Map<String, Object> getRecommendations() {
        Map<String, Object> result = new HashMap<>();
        
        // 获取推荐商品
        List<Product> recommendedProducts = getProductsByTimeSlot(getCurrentTimeSlot());
        result.put("recommendedProducts", recommendedProducts);
        
        // 获取生活美学文章
        List<Map<String, Object>> lifestyleArticles = productMapper.findLifestyleArticles();
        result.put("lifestyleArticles", lifestyleArticles);
        
        // 获取热点话题
        List<Map<String, Object>> hotTopics = productMapper.findHotTopics();
        result.put("hotTopics", hotTopics);
        
        return result;
    }
    
    // 获取产品详情
    public ProductDetail getProductDetail(Long id) {
        return productMapper.findProductDetailById(id);
    }
    
    // 获取产品评论
    public Map<String, Object> getProductComments(Long id, int page, int size) {
        return productMapper.findProductComments(id, page, size);
    }
    
    // 获取相关产品
    public List<Product> getRelatedProducts(Long id, Integer categoryId) {
        return productMapper.findRelatedProducts(id, categoryId);
    }
    
    // 检查产品库存
    public Map<String, Object> checkProductStock(Long id, Map<String, String> specs) {
        return productMapper.checkProductStock(id, specs);
    }
    
    // 获取规格价格
    public Map<String, Object> getSpecPrice(Long id, Map<String, String> specs) {
        return productMapper.getSpecPrice(id, specs);
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