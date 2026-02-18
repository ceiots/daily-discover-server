package com.dailydiscover.service;

import com.dailydiscover.model.ProductDetail;

public interface ProductDetailService {
    
    /**
     * 根据商品ID查询商品详情
     */
    ProductDetail findByProductId(Long productId);
}