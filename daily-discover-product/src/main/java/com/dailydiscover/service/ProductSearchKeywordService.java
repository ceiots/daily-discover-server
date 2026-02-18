package com.dailydiscover.service;

import com.dailydiscover.model.ProductSearchKeyword;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 商品搜索关键词服务接口
 */
public interface ProductSearchKeywordService extends IService<ProductSearchKeyword> {
    
    /**
     * 记录搜索关键词
     */
    void recordSearch(String keyword);
    
    /**
     * 查询搜索建议
     */
    List<String> findSuggestions(String keyword);
    
    /**
     * 查询热门搜索关键词
     */
    List<String> findHotKeywords(int limit);
    
    /**
     * 根据关键词查询搜索统计
     */
    ProductSearchKeyword findByKeyword(String keyword);
    
    /**
     * 根据关键词获取搜索记录
     */
    ProductSearchKeyword getByKeyword(String keyword);
    
    /**
     * 获取热门搜索关键词
     */
    List<ProductSearchKeyword> getPopularKeywords(int limit);
    
    /**
     * 获取趋势搜索关键词
     */
    List<ProductSearchKeyword> getTrendingKeywords(int limit);
    
    /**
     * 增加搜索次数
     */
    boolean incrementSearchCount(Long id);
}