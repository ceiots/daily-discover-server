package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductDetailMapper;
import com.dailydiscover.model.ProductDetail;
import com.dailydiscover.service.ProductDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProductDetailServiceImpl extends ServiceImpl<ProductDetailMapper, ProductDetail> implements ProductDetailService {
    
    @Autowired
    private ProductDetailMapper productDetailMapper;
    
    @Override
    public ProductDetail findByProductId(Long productId) {
        return productDetailMapper.findByProductId(productId);
    }
}