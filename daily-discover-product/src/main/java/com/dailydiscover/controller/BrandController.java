package com.dailydiscover.controller;

import com.dailydiscover.mapper.BrandMapper;
import com.dailydiscover.model.Brand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/brands")
public class BrandController {
    
    @Autowired
    private BrandMapper brandMapper;
    
    // 获取生活方式品牌数据
    @GetMapping("/lifestyle")
    public List<Map<String, Object>> getLifestyleBrands() {
        List<Map<String, Object>> brands = new ArrayList<>();
        
        try {
            // 从数据库获取品牌数据
            List<Brand> brandList = brandMapper.findAll();
            
            for (Brand brand : brandList) {
                Map<String, Object> brandMap = new HashMap<>();
                brandMap.put("id", brand.getId());
                brandMap.put("name", brand.getName());
                brandMap.put("logo", brand.getLogo());
                brandMap.put("description", brand.getDescription());
                brandMap.put("category", brand.getCategory());
                brandMap.put("followers", brand.getFollowers());
                brandMap.put("trend", brand.getTrend());
                brandMap.put("featured", brand.getFeatured());
                brands.add(brandMap);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            // 如果数据库查询失败，返回空列表而不是报错
            return brands;
        }
        
        return brands;
    }
    
    // 获取精选品牌
    @GetMapping("/featured")
    public List<Map<String, Object>> getFeaturedBrands() {
        List<Map<String, Object>> brands = new ArrayList<>();
        
        try {
            List<Brand> brandList = brandMapper.findFeaturedBrands();
            
            for (Brand brand : brandList) {
                Map<String, Object> brandMap = new HashMap<>();
                brandMap.put("id", brand.getId());
                brandMap.put("name", brand.getName());
                brandMap.put("logo", brand.getLogo());
                brandMap.put("description", brand.getDescription());
                brandMap.put("category", brand.getCategory());
                brandMap.put("followers", brand.getFollowers());
                brandMap.put("trend", brand.getTrend());
                brandMap.put("featured", brand.getFeatured());
                brands.add(brandMap);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return brands;
        }
        
        return brands;
    }
}