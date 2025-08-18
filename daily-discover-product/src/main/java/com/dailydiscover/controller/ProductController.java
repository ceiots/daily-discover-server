package com.dailydiscover.controller;

import com.dailydiscover.model.ProductEntity;
import com.dailydiscover.service.ProductService;
import com.dailydiscover.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private RecommendationService recommendationService;
    
    // 获取所有活跃商品
    @GetMapping
    public List<ProductEntity> getAllProducts() {
        return productService.getAllActiveProducts();
    }
    
    // 根据ID获取商品
    @GetMapping("/{id}")
    public ResponseEntity<ProductEntity> getProductById(@PathVariable Long id) {
        ProductEntity product = productService.getProductById(id);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 根据时间段获取推荐商品（支持数字和字符串格式）
    @GetMapping("/recommendations")
    public List<ProductEntity> getRecommendations(@RequestParam(required = false) Integer timeSlot) {
        String timeSlotStr;
        if (timeSlot != null) {
            // 前端传递数字时间段，转换为字符串
            timeSlotStr = convertTimeSlotToString(timeSlot);
        } else {
            // 没有传递参数，使用当前时间段
            timeSlotStr = getCurrentTimeSlot();
        }
        return productService.getProductsByTimeSlot(timeSlotStr);
    }
    
    // 兼容旧版本的路径格式
    @GetMapping("/recommendations/{timeSlot}")
    public List<ProductEntity> getRecommendationsByTimeSlot(@PathVariable String timeSlot) {
        return productService.getProductsByTimeSlot(timeSlot);
    }
    
    // 根据分类获取商品
    @GetMapping("/category/{category}")
    public List<ProductEntity> getProductsByCategory(@PathVariable String category) {
        return productService.getProductsByCategory(category);
    }
    
    // 搜索商品
    @GetMapping("/search")
    public List<ProductEntity> searchProducts(@RequestParam String keyword) {
        return productService.searchProducts(keyword);
    }
    
    // 创建新商品
    @PostMapping
    public ProductEntity createProduct(@RequestBody ProductEntity product) {
        return productService.createProduct(product);
    }
    
    // 更新商品
    @PutMapping("/{id}")
    public ResponseEntity<ProductEntity> updateProduct(@PathVariable Long id, @RequestBody ProductEntity productDetails) {
        try {
            ProductEntity updatedProduct = productService.updateProduct(id, productDetails);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 删除商品
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 获取最新商品
    @GetMapping("/latest")
    public List<ProductEntity> getLatestProducts() {
        return productService.getLatestProducts();
    }
    
    // 获取智能推荐商品
    @GetMapping("/smart-recommendations")
    public List<ProductEntity> getSmartRecommendations(@RequestParam(defaultValue = "6") int limit) {
        return recommendationService.getSmartRecommendations(limit);
    }
    
    // 获取个性化推荐商品
    @GetMapping("/personalized-recommendations")
    public List<ProductEntity> getPersonalizedRecommendations(
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "6") int limit) {
        return recommendationService.getPersonalizedRecommendations(userId, limit);
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
    
    // 将数字时间段转换为字符串
    private String convertTimeSlotToString(Integer timeSlot) {
        if (timeSlot == null) {
            return getCurrentTimeSlot();
        }
        switch (timeSlot) {
            case 0: // MORNING
                return "morning";
            case 1: // NOON
                return "noon";
            case 2: // AFTERNOON
                return "afternoon";
            case 3: // EVENING
                return "evening";
            default:
                return getCurrentTimeSlot();
        }
    }
    
    // 从DiscoverDataController整合的商品相关功能
    
    // 获取今日商品
    @GetMapping("/today")
    public List<ProductEntity> getTodayProducts() {
        return productService.getTodayProducts();
    }
    
    // 获取昨日商品
    @GetMapping("/yesterday")
    public List<ProductEntity> getYesterdayProducts() {
        return productService.getYesterdayProducts();
    }
    
    // 根据主题获取商品
    @GetMapping("/theme/{themeId}")
    public List<ProductEntity> getProductsByTheme(@PathVariable Long themeId) {
        return productService.getProductsByTheme(themeId);
    }
    
    // 获取所有活跃商品（与根路径相同，提供更多选择）
    @GetMapping("/all")
    public List<ProductEntity> getAllActiveProductsAlternative() {
        return productService.getAllActiveProducts();
    }
    
    // 初始化示例商品数据
    @PostMapping("/initialize")
    public ResponseEntity<Map<String, Object>> initializeSampleProducts() {
        try {
            productService.initializeSampleProducts();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "示例商品数据初始化成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "初始化失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}