package com.dailydiscover.service;

import com.dailydiscover.model.DiscoverPage;
import com.dailydiscover.model.Motto;
import com.dailydiscover.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class DiscoverService {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private MottoService mottoService;
    
    @Autowired
    private DiscoverPageRepository discoverPageRepository;
    
    // 获取今日发现页面
    public DiscoverPage getTodayDiscover() {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        return getDiscoverByDate(today);
    }
    
    // 根据日期获取发现页面
    public DiscoverPage getDiscoverByDate(String date) {
        // 这里应该从数据库查询，暂时返回模拟数据
        DiscoverPage discoverPage = new DiscoverPage();
        discoverPage.setTitle("晨光里的温柔");
        discoverPage.setSubtitle("周一 · 重启日");
        discoverPage.setThemeImage("https://picsum.photos/id/175/1200/400");
        discoverPage.setDateInfo(date);
        
        // 获取今日箴言
        List<Motto> todayMottos = getTodayMottos();
        discoverPage.setMottos(todayMottos);
        
        // 获取推荐商品
        List<Product> recommendedProducts = getRecommendedProducts(6);
        discoverPage.setRecommendedProducts(recommendedProducts);
        
        return discoverPage;
    }
    
    // 获取所有活跃箴言
    public List<Motto> getAllActiveMottos() {
        return mottoService.getAllActiveMottos();
    }
    
    // 获取今日箴言
    public List<Motto> getTodayMottos() {
        // 暂时返回固定的箴言列表
        return List.of(
            new Motto("把今天过成值得收藏的日子", "生活美学", 1),
            new Motto("慢下来，感受此刻的温度", "生活美学", 2),
            new Motto("生活的美学，藏在每一个细节里", "生活美学", 3)
        );
    }
    
    // 获取推荐商品
    public List<Product> getRecommendedProducts(int limit) {
        // 暂时返回固定的推荐商品列表
        return List.of(
            new Product("手冲咖啡壶套装", new java.math.BigDecimal("299.00"), 
                       "一杯咖啡的时间，让生活慢下来", 
                       "https://picsum.photos/id/431/600/800", 
                       "morning", "咖啡器具"),
            new Product("亚麻棉麻抱枕", new java.math.BigDecimal("129.00"), 
                       "午后小憩的温柔陪伴", 
                       "https://picsum.photos/id/20/600/800", 
                       "afternoon", "家居用品"),
            new Product("陶瓷花瓶", new java.math.BigDecimal("89.00"), 
                       "为生活增添一抹自然色彩", 
                       "https://picsum.photos/id/1015/600/800", 
                       "morning", "家居装饰"),
            new Product("香薰蜡烛", new java.math.BigDecimal("59.00"), 
                       "温暖的光，治愈的香", 
                       "https://picsum.photos/id/1039/600/800", 
                       "evening", "香氛用品"),
            new Product("木质书架", new java.math.BigDecimal("199.00"), 
                       "收纳生活的美好", 
                       "https://picsum.photos/id/1059/600/800", 
                       "afternoon", "家居用品"),
            new Product("茶具套装", new java.math.BigDecimal("159.00"), 
                       "品茶时光，静享生活", 
                       "https://picsum.photos/id/1062/600/800", 
                       "morning", "茶具用品")
        );
    }
    
    // 创建发现页面
    public DiscoverPage createDiscoverPage(DiscoverPage discoverPage) {
        return discoverPageRepository.save(discoverPage);
    }
    
    // 更新发现页面
    public DiscoverPage updateDiscoverPage(Long id, DiscoverPage discoverPageDetails) {
        DiscoverPage discoverPage = discoverPageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("DiscoverPage not found with id: " + id));
        
        discoverPage.setTitle(discoverPageDetails.getTitle());
        discoverPage.setSubtitle(discoverPageDetails.getSubtitle());
        discoverPage.setThemeImage(discoverPageDetails.getThemeImage());
        discoverPage.setDateInfo(discoverPageDetails.getDateInfo());
        discoverPage.setMottos(discoverPageDetails.getMottos());
        discoverPage.setRecommendedProducts(discoverPageDetails.getRecommendedProducts());
        
        return discoverPageRepository.save(discoverPage);
    }
    
    // 删除发现页面
    public void deleteDiscoverPage(Long id) {
        discoverPageRepository.deleteById(id);
    }
}