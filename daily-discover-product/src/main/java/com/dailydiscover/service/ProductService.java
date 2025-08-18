package com.dailydiscover.service;

import com.dailydiscover.model.ProductEntity;
import java.util.List;

public interface ProductService {
    
    // 获取今日商品
    List<ProductEntity> getTodayProducts();
    
    // 获取昨日商品
    List<ProductEntity> getYesterdayProducts();
}