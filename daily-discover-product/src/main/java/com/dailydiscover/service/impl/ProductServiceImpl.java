package com.dailydiscover.service;

import com.dailydiscover.mapper.ProductMapper;
import com.dailydiscover.model.ProductEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    public List<ProductEntity> getTodayProducts() {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        return productMapper.findTodayProducts(today);
    }
    
    @Override
    public List<ProductEntity> getYesterdayProducts() {
        String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE);
        return productMapper.findYesterdayProducts(yesterday);
    }
}