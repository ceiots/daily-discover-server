package com.example.service;

import org.springframework.transaction.annotation.Transactional;

import com.example.model.ProductSearchKeyword;

import java.util.List;

public interface ProductSearchKeywordService {

    /**
     * 创建商品搜索关键词
     */
    @Transactional
    ProductSearchKeyword create(ProductSearchKeyword keyword);
    
    /**
     * 批量创建商品搜索关键词
     */
    @Transactional
    List<ProductSearchKeyword> batchCreate(List<ProductSearchKeyword> keywords);
    
    /**
     * 根据ID获取关键词
     */
    ProductSearchKeyword getById(Long id);
    
    /**
     * 根据商品ID获取关键词列表
     */
    List<ProductSearchKeyword> getByProductId(Long productId);
    
    /**
     * 搜索关键词
     */
    List<ProductSearchKeyword> searchKeywords(String keyword, Integer limit);
    
    /**
     * 根据关键词搜索商品ID列表
     */
    List<Long> searchProductIds(String keyword, Integer limit);
    
    /**
     * 更新关键词
     */
    @Transactional
    boolean update(ProductSearchKeyword keyword);
    
    /**
     * 删除关键词
     */
    @Transactional
    boolean delete(Long id);
    
    /**
     * 根据商品ID删除所有关键词
     */
    @Transactional
    boolean deleteByProductId(Long productId);
    
    /**
     * 自动生成商品关键词
     * 分析商品标题、描述等信息，生成可能的搜索关键词
     */
    @Transactional
    List<ProductSearchKeyword> generateKeywords(Long productId);
    
    /**
     * 更新关键词权重
     * 当用户搜索并点击商品时，增加相应关键词的权重
     */
    @Transactional
    boolean increaseWeight(Long keywordId);
} 