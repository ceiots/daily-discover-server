package com.dailydiscover.controller;

import com.dailydiscover.model.DiscoverPage;
import com.dailydiscover.model.Motto;
import com.dailydiscover.model.Product;
import com.dailydiscover.service.DiscoverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/discover")
public class DiscoverController {
    
    @Autowired
    private DiscoverService discoverService;
    
    // 获取今日发现页面数据
    @GetMapping("/today")
    public ResponseEntity<DiscoverPage> getTodayDiscover() {
        DiscoverPage discoverPage = discoverService.getTodayDiscover();
        if (discoverPage != null) {
            return ResponseEntity.ok(discoverPage);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 获取发现页面数据（根据日期）
    @GetMapping("/date/{date}")
    public ResponseEntity<DiscoverPage> getDiscoverByDate(@PathVariable String date) {
        DiscoverPage discoverPage = discoverService.getDiscoverByDate(date);
        if (discoverPage != null) {
            return ResponseEntity.ok(discoverPage);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 获取所有箴言
    @GetMapping("/mottos")
    public List<Motto> getAllMottos() {
        return discoverService.getAllActiveMottos();
    }
    
    // 获取今日箴言
    @GetMapping("/mottos/today")
    public List<Motto> getTodayMottos() {
        return discoverService.getTodayMottos();
    }
    
    // 获取推荐商品
    @GetMapping("/products/recommended")
    public List<Product> getRecommendedProducts(@RequestParam(defaultValue = "6") int limit) {
        return discoverService.getRecommendedProducts(limit);
    }
    
    // 创建新的发现页面
    @PostMapping
    public DiscoverPage createDiscoverPage(@RequestBody DiscoverPage discoverPage) {
        return discoverService.createDiscoverPage(discoverPage);
    }
    
    // 更新发现页面
    @PutMapping("/{id}")
    public ResponseEntity<DiscoverPage> updateDiscoverPage(@PathVariable Long id, @RequestBody DiscoverPage discoverPageDetails) {
        try {
            DiscoverPage updatedPage = discoverService.updateDiscoverPage(id, discoverPageDetails);
            return ResponseEntity.ok(updatedPage);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 删除发现页面
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiscoverPage(@PathVariable Long id) {
        try {
            discoverService.deleteDiscoverPage(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}