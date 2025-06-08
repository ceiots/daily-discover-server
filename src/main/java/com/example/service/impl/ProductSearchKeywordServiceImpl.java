package com.example.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.common.exception.ApiException;
import com.example.mapper.ProductMapper;
import com.example.mapper.ProductSearchKeywordMapper;
import com.example.model.Product;
import com.example.model.ProductSearchKeyword;
import com.example.service.ProductSearchKeywordService;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ProductSearchKeywordServiceImpl implements ProductSearchKeywordService {

    @Autowired
    private ProductSearchKeywordMapper keywordMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    @Transactional
    public ProductSearchKeyword create(ProductSearchKeyword keyword) {
        try {
            keywordMapper.insert(keyword);
            return keywordMapper.findById(keyword.getId());
        } catch (Exception e) {
            log.error("创建商品搜索关键词失败", e);
            throw new ApiException("创建商品搜索关键词失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public List<ProductSearchKeyword> batchCreate(List<ProductSearchKeyword> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<ProductSearchKeyword> result = new ArrayList<>();
        for (ProductSearchKeyword keyword : keywords) {
            try {
                keywordMapper.insert(keyword);
                result.add(keywordMapper.findById(keyword.getId()));
            } catch (Exception e) {
                log.error("批量创建商品搜索关键词失败", e);
            }
        }
        return result;
    }
    
    @Override
    public ProductSearchKeyword getById(Long id) {
        return keywordMapper.findById(id);
    }
    
    @Override
    public List<ProductSearchKeyword> getByProductId(Long productId) {
        return keywordMapper.findByProductId(productId);
    }
    
    @Override
    public List<ProductSearchKeyword> searchKeywords(String keyword, Integer limit) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        if (limit == null || limit <= 0) {
            limit = 10; // 默认限制10个结果
        }
        
        return keywordMapper.findByKeyword(keyword.trim(), limit);
    }
    
    @Override
    public List<Long> searchProductIds(String keyword, Integer limit) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        if (limit == null || limit <= 0) {
            limit = 20; // 默认限制20个结果
        }
        
        return keywordMapper.findProductIdsByKeyword(keyword.trim(), limit);
    }
    
    @Override
    @Transactional
    public boolean update(ProductSearchKeyword keyword) {
        try {
            int rows = keywordMapper.update(keyword);
            return rows > 0;
        } catch (Exception e) {
            log.error("更新商品搜索关键词失败", e);
            throw new ApiException("更新商品搜索关键词失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean delete(Long id) {
        try {
            int rows = keywordMapper.deleteById(id);
            return rows > 0;
        } catch (Exception e) {
            log.error("删除商品搜索关键词失败", e);
            throw new ApiException("删除商品搜索关键词失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean deleteByProductId(Long productId) {
        try {
            int rows = keywordMapper.deleteByProductId(productId);
            return rows > 0;
        } catch (Exception e) {
            log.error("删除商品所有搜索关键词失败", e);
            throw new ApiException("删除商品所有搜索关键词失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public List<ProductSearchKeyword> generateKeywords(Long productId) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            throw new ApiException("商品不存在");
        }
        
        // 首先删除已有的自动生成关键词
        try {
            keywordMapper.deleteByProductId(productId);
            
            // 生成新的关键词
            List<ProductSearchKeyword> keywords = new ArrayList<>();
            
            // 从标题中提取关键词
            if (product.getTitle() != null) {
                Set<String> titleKeywords = extractKeywords(product.getTitle());
                for (String keyword : titleKeywords) {
                    ProductSearchKeyword kw = new ProductSearchKeyword();
                    kw.setProductId(productId);
                    kw.setKeyword(keyword);
                    kw.setWeight(100); // 标题关键词权重高
                    kw.setIsManual(false);
                    keywords.add(kw);
                }
            }
            
            // 从描述中提取关键词
            if (product.getDescription() != null) {
                Set<String> descKeywords = extractKeywords(product.getDescription());
                for (String keyword : descKeywords) {
                    // 避免重复添加
                    boolean exists = false;
                    for (ProductSearchKeyword kw : keywords) {
                        if (kw.getKeyword().equals(keyword)) {
                            exists = true;
                            break;
                        }
                    }
                    
                    if (!exists) {
                        ProductSearchKeyword kw = new ProductSearchKeyword();
                        kw.setProductId(productId);
                        kw.setKeyword(keyword);
                        kw.setWeight(50); // 描述关键词权重较低
                        kw.setIsManual(false);
                        keywords.add(kw);
                    }
                }
            }
            
            // 批量创建关键词
            return batchCreate(keywords);
        } catch (Exception e) {
            log.error("生成商品搜索关键词失败", e);
            throw new ApiException("生成商品搜索关键词失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public boolean increaseWeight(Long keywordId) {
        ProductSearchKeyword keyword = keywordMapper.findById(keywordId);
        if (keyword == null) {
            return false;
        }
        
        try {
            // 增加权重
            keyword.setWeight(keyword.getWeight() + 1);
            return update(keyword);
        } catch (Exception e) {
            log.error("增加关键词权重失败", e);
            return false;
        }
    }
    
    /**
     * 从文本中提取关键词
     */
    private Set<String> extractKeywords(String text) {
        Set<String> keywords = new HashSet<>();
        if (text == null || text.trim().isEmpty()) {
            return keywords;
        }
        
        // 中文分词的简单实现（实际项目中可能需要使用专业分词库）
        // 这里简单处理：去除标点符号，按空格分割
        String cleaned = text.replaceAll("[\\p{P}\\p{S}]", " ").trim();
        String[] words = cleaned.split("\\s+");
        
        for (String word : words) {
            if (word.length() >= 2) { // 忽略单字
                keywords.add(word);
            }
        }
        
        // 尝试提取中文短语（2-4个字符）
        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]{2,4}");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            keywords.add(matcher.group());
        }
        
        return keywords;
    }
} 