package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.ProductSearchKeywordMapper;
import com.dailydiscover.model.ProductSearchKeyword;
import com.dailydiscover.service.ProductSearchKeywordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductSearchKeywordServiceImpl extends ServiceImpl<ProductSearchKeywordMapper, ProductSearchKeyword> implements ProductSearchKeywordService {
    
    @Autowired
    private ProductSearchKeywordMapper productSearchKeywordMapper;
    
    @Override
    public void recordSearch(String keyword) {
        productSearchKeywordMapper.recordSearch(keyword);
    }
    
    @Override
    public List<String> findSuggestions(String keyword) {
        return productSearchKeywordMapper.findSuggestions(keyword);
    }
    
    @Override
    public List<String> findHotKeywords(int limit) {
        return productSearchKeywordMapper.findHotKeywords(limit);
    }
    
    @Override
    public ProductSearchKeyword findByKeyword(String keyword) {
        return productSearchKeywordMapper.findByKeyword(keyword);
    }
}