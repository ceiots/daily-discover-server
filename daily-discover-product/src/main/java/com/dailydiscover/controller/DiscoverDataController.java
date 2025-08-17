package com.dailydiscover.controller;

import com.dailydiscover.model.DailyMotto;
import com.dailydiscover.model.DailyTheme;
import com.dailydiscover.model.ProductEntity;
import com.dailydiscover.service.DiscoverDataService;
import org.springframework.beans.factory.annotation.Autowired;
import com.dailydiscover.common.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class DiscoverDataController {
    
    @Autowired
    private DiscoverDataService discoverDataService;
    
    // 获取今日箴言
    @GetMapping("/mottos/today")
    public ApiResponse<List<DailyMotto>> getTodayMottos() {
        try {
            List<DailyMotto> mottos = discoverDataService.getTodayMottos();
            return ApiResponse.success(mottos);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取今日箴言失败: " + e.getMessage());
        }
    }
    
    // 获取今日商品
    @GetMapping("/products/today")
    public ApiResponse<List<ProductEntity>> getTodayProducts() {
        try {
            List<ProductEntity> products = discoverDataService.getTodayProducts();
            return ApiResponse.success(products);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取今日推荐商品失败: " + e.getMessage());
        }
    }
    
    // 获取今日主题
    @GetMapping("/themes/today")
    public ApiResponse<DailyTheme> getTodayTheme() {
        try {
            DailyTheme theme = discoverDataService.getTodayTheme();
            return ApiResponse.success(theme);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取今日主题失败: " + e.getMessage());
        }
    }
    
    // 获取昨日商品
    @GetMapping("/products/yesterday")
    public ApiResponse<List<ProductEntity>> getYesterdayProducts() {
        try {
            List<ProductEntity> products = discoverDataService.getYesterdayProducts();
            return ApiResponse.success(products);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取昨日推荐商品失败: " + e.getMessage());
        }
    }
    
    // 获取昨日主题
    @GetMapping("/themes/yesterday")
    public ApiResponse<DailyTheme> getYesterdayTheme() {
        try {
            DailyTheme theme = discoverDataService.getYesterdayTheme();
            return ApiResponse.success(theme);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取昨日主题失败: " + e.getMessage());
        }
    }
    
    // 根据主题获取商品
    @GetMapping("/products/theme/{themeId}")
    public ApiResponse<List<ProductEntity>> getProductsByTheme(@PathVariable Long themeId) {
        try {
            List<ProductEntity> products = discoverDataService.getProductsByTheme(themeId);
            return ApiResponse.success(products);
        } catch (Exception e) {
            return ApiResponse.error(500, "根据主题获取商品失败: " + e.getMessage());
        }
    }
    
    // 获取所有活跃箴言
    @GetMapping("/mottos/all")
    public ApiResponse<List<DailyMotto>> getAllActiveMottos() {
        try {
            List<DailyMotto> mottos = discoverDataService.getAllActiveMottos();
            return ApiResponse.success(mottos);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取所有活跃箴言失败: " + e.getMessage());
        }
    }
    
    // 获取所有活跃主题
    @GetMapping("/themes/all")
    public ApiResponse<List<DailyTheme>> getAllActiveThemes() {
        try {
            List<DailyTheme> themes = discoverDataService.getAllActiveThemes();
            return ApiResponse.success(themes);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取所有活跃主题失败: " + e.getMessage());
        }
    }
    
    // 获取所有活跃商品
    @GetMapping("/products/all")
    public ApiResponse<List<ProductEntity>> getAllActiveProducts() {
        try {
            List<ProductEntity> products = discoverDataService.getAllActiveProducts();
            return ApiResponse.success(products);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取所有活跃商品失败: " + e.getMessage());
        }
    }
    
    // 初始化示例数据
    @PostMapping("/initialize")
    public ApiResponse<String> initializeSampleData() {
        try {
            discoverDataService.initializeSampleData();
            return ApiResponse.success("Sample data initialized successfully");
        } catch (Exception e) {
            return ApiResponse.error(500, "Failed to initialize sample data: " + e.getMessage());
        }
    }
    
    // 健康检查接口
    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("Discover Data Service is running");
    }
}