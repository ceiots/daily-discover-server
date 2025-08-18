package com.dailydiscover.controller;

import com.dailydiscover.model.ProductEntity;
import com.dailydiscover.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
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
}