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
    
    @Override
    public ProductSearchKeyword getByKeyword(String keyword) {
        // 使用 Mapper 方法查询
        return productSearchKeywordMapper.findByKeyword(keyword);
    }
    
    @Override
    public List<ProductSearchKeyword> getPopularKeywords(int limit) {
        // 使用 MyBatis-Plus 的 lambda 查询实现热门关键词查询
        return lambdaQuery()
                .eq(ProductSearchKeyword::getIsDeleted, 0)
                .orderByDesc(ProductSearchKeyword::getSearchCount)
                .last("LIMIT " + limit)
                .list();
    }
    
    @Override
    public List<ProductSearchKeyword> getTrendingKeywords(int limit) {
        // 使用 MyBatis-Plus 的 lambda 查询实现趋势关键词查询（按最近搜索次数排序）
        return lambdaQuery()
                .eq(ProductSearchKeyword::getIsDeleted, 0)
                .orderByDesc(ProductSearchKeyword::getRecentSearchCount)
                .last("LIMIT " + limit)
                .list();
    }
    
    @Override
    public boolean incrementSearchCount(Long id) {
        ProductSearchKeyword keyword = getById(id);
        if (keyword != null) {
            keyword.setSearchCount(keyword.getSearchCount() + 1);
            keyword.setRecentSearchCount(keyword.getRecentSearchCount() + 1);
            return updateById(keyword);
        }
        return false;
    }
}