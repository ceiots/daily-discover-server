package com.dailydiscover.service;

import com.dailydiscover.model.DailyMotto;
import com.dailydiscover.model.DailyTheme;
import com.dailydiscover.model.ProductEntity;
import java.util.List;

public interface DiscoverDataService {
    
    // 获取今日箴言
    List<DailyMotto> getTodayMottos();
    
    // 获取今日商品
    List<ProductEntity> getTodayProducts();
    
    // 获取今日主题
    DailyTheme getTodayTheme();
    
    // 获取昨日商品
    List<ProductEntity> getYesterdayProducts();
    
    // 获取昨日主题
    DailyTheme getYesterdayTheme();
    
    // 初始化示例数据
    void initializeSampleData();
    
    // 根据主题获取商品
    List<ProductEntity> getProductsByTheme(Long themeId);
    
    // 获取所有活跃箴言
    List<DailyMotto> getAllActiveMottos();
    
    // 获取所有活跃主题
    List<DailyTheme> getAllActiveThemes();
    
    // 获取所有活跃商品
    List<ProductEntity> getAllActiveProducts();
}