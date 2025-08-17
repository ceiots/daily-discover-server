package com.dailydiscover.service;

import com.dailydiscover.mapper.DailyMottoMapper;
import com.dailydiscover.mapper.DailyThemeMapper;
import com.dailydiscover.mapper.DiscoverProductMapper;
import com.dailydiscover.model.DailyMotto;
import com.dailydiscover.model.DailyTheme;
import com.dailydiscover.model.ProductEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class DiscoverDataServiceImpl implements DiscoverDataService {
    
    @Autowired
    private DailyMottoMapper dailyMottoMapper;
    
    @Autowired
    private DailyThemeMapper dailyThemeMapper;
    
    @Autowired
    private DiscoverProductMapper discoverProductMapper;
    
    @Override
    public List<DailyMotto> getTodayMottos() {
        List<DailyMotto> mottos = dailyMottoMapper.findTodayMottos();
        if (mottos.isEmpty()) {
            // 如果没有今日箴言，返回示例数据
            return Arrays.asList(
                new DailyMotto("把今天过成值得收藏的日子", LocalDate.now()),
                new DailyMotto("慢下来，感受此刻的温度", LocalDate.now()),
                new DailyMotto("生活中的美好，藏在细节里", LocalDate.now())
            );
        }
        return mottos;
    }
    
    @Override
    public List<ProductEntity> getTodayProducts() {
        List<ProductEntity> products = discoverProductMapper.findTodayProducts();
        if (products.isEmpty()) {
            // 如果没有今日商品，返回示例数据
            return Arrays.asList(
                new ProductEntity("手工陶瓷咖啡杯", "一杯咖啡的时间，让生活慢下来", new BigDecimal("128.00"), 
                    "https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=400&h=400&fit=crop", 2L),
                new ProductEntity("亚麻抱枕", "柔软触感，温暖陪伴", new BigDecimal("89.00"), 
                    "https://images.unsplash.com/photo-1584184724797-1b4f5d1b0c4d?w=400&h=400&fit=crop", 1L),
                new ProductEntity("香薰蜡烛", "温暖的光，治愈的味道", new BigDecimal("68.00"), 
                    "https://images.unsplash.com/photo-1596558890593-1d4b0b5c6e6d?w=400&h=400&fit=crop", 1L)
            );
        }
        return products;
    }
    
    @Override
    public DailyTheme getTodayTheme() {
        List<DailyTheme> themes = dailyThemeMapper.findTodayThemes();
        if (themes.isEmpty()) {
            // 如果没有今日主题，返回示例数据
            return new DailyTheme("晨光里的温柔", "周一重启日", 
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=400&fit=crop", 
                LocalDate.now(), "重启日");
        }
        return themes.get(0);
    }
    
    @Override
    public List<ProductEntity> getYesterdayProducts() {
        List<ProductEntity> products = discoverProductMapper.findYesterdayProducts();
        if (products.isEmpty()) {
            // 如果没有昨日商品，返回示例数据
            return Arrays.asList(
                new ProductEntity("玻璃花瓶", "简约设计，装点生活", new BigDecimal("156.00"), 
                    "https://images.unsplash.com/photo-1584184724797-1b4f5d1b0c4d?w=400&h=400&fit=crop", 3L),
                new ProductEntity("羊毛围巾", "温暖柔软，冬日必备", new BigDecimal("238.00"), 
                    "https://images.unsplash.com/photo-1596558890593-1d4b0b5c6e6d?w=400&h=400&fit=crop", 4L),
                new ProductEntity("木质餐具套装", "自然材质，健康生活", new BigDecimal("198.00"), 
                    "https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=400&h=400&fit=crop", 2L)
            );
        }
        return products;
    }
    
    @Override
    public DailyTheme getYesterdayTheme() {
        List<DailyTheme> themes = dailyThemeMapper.findYesterdayThemes();
        if (themes.isEmpty()) {
            // 如果没有昨日主题，返回示例数据
            return new DailyTheme("午后的悠闲", "周二放松日", 
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=400&fit=crop", 
                LocalDate.now().minusDays(1), "放松日");
        }
        return themes.get(0);
    }
    
    @Override
    public void initializeSampleData() {
        // 初始化箴言数据
        List<DailyMotto> sampleMottos = Arrays.asList(
            new DailyMotto("把今天过成值得收藏的日子", LocalDate.now()),
            new DailyMotto("慢下来，感受此刻的温度", LocalDate.now()),
            new DailyMotto("生活中的美好，藏在细节里", LocalDate.now()),
            new DailyMotto("用心感受，每个瞬间都值得珍藏", LocalDate.now()),
            new DailyMotto("让生活充满仪式感，从今天开始", LocalDate.now())
        );
        
        for (DailyMotto motto : sampleMottos) {
            dailyMottoMapper.insert(motto);
        }
        
        // 初始化主题数据
        List<DailyTheme> sampleThemes = Arrays.asList(
            new DailyTheme("晨光里的温柔", "周一重启日", 
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=400&fit=crop", 
                LocalDate.now(), "重启日"),
            new DailyTheme("午后的悠闲", "周二放松日", 
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=400&fit=crop", 
                LocalDate.now(), "放松日"),
            new DailyTheme("傍晚的宁静", "周三静心日", 
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=400&fit=crop", 
                LocalDate.now(), "静心日")
        );
        
        for (DailyTheme theme : sampleThemes) {
            dailyThemeMapper.insert(theme);
        }
        
        // 初始化商品数据
        List<ProductEntity> sampleProducts = Arrays.asList(
            new ProductEntity("手工陶瓷咖啡杯", "一杯咖啡的时间，让生活慢下来", new BigDecimal("128.00"), 
                "https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=400&h=400&fit=crop", 2L),
            new ProductEntity("亚麻抱枕", "柔软触感，温暖陪伴", new BigDecimal("89.00"), 
                "https://images.unsplash.com/photo-1584184724797-1b4f5d1b0c4d?w=400&h=400&fit=crop", 1L),
            new ProductEntity("香薰蜡烛", "温暖的光，治愈的味道", new BigDecimal("68.00"), 
                "https://images.unsplash.com/photo-1596558890593-1d4b0b5c6e6d?w=400&h=400&fit=crop", 1L),
            new ProductEntity("玻璃花瓶", "简约设计，装点生活", new BigDecimal("156.00"), 
                "https://images.unsplash.com/photo-1584184724797-1b4f5d1b0c4d?w=400&h=400&fit=crop", 3L),
            new ProductEntity("羊毛围巾", "温暖柔软，冬日必备", new BigDecimal("238.00"), 
                "https://images.unsplash.com/photo-1596558890593-1d4b0b5c6e6d?w=400&h=400&fit=crop", 4L),
            new ProductEntity("木质餐具套装", "自然材质，健康生活", new BigDecimal("198.00"), 
                "https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=400&h=400&fit=crop", 2L)
        );
        
        for (ProductEntity product : sampleProducts) {
            discoverProductMapper.insert(product);
        }
    }
    
    @Override
    public List<ProductEntity> getProductsByTheme(Long themeId) {
        return discoverProductMapper.findProductsByTheme(themeId, LocalDate.now());
    }
    
    @Override
    public List<DailyMotto> getAllActiveMottos() {
        return dailyMottoMapper.findAllActiveMottos();
    }
    
    @Override
    public List<DailyTheme> getAllActiveThemes() {
        return dailyThemeMapper.findAllActiveThemes();
    }
    
    @Override
    public List<ProductEntity> getAllActiveProducts() {
        return discoverProductMapper.findAllActiveProducts();
    }
}