package com.dailydiscover.service;

import com.dailydiscover.model.ProductSearchKeyword;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 商品搜索关键词服务接口
 */
public interface ProductSearchKeywordService extends IService<ProductSearchKeyword> {
    
    /**
     * 根据关键词查询搜索记录
     * @param keyword 关键词
     * @return 搜索记录
     */
    ProductSearchKeyword getByKeyword(String keyword);
    
    /**
     * 增加搜索次数
     * @param keyword 关键词
     * @return 是否成功
     */
    boolean incrementSearchCount(String keyword);
    
    /**
     * 获取热门搜索关键词
     * @param limit 限制数量
     * @return 热门关键词列表
     */
    java.util.List<ProductSearchKeyword> getHotKeywords(int limit);
    
    /**
     * 获取推荐搜索关键词
     * @param limit 限制数量
     * @return 推荐关键词列表
     */
    java.util.List<ProductSearchKeyword> getRecommendedKeywords(int limit);
}