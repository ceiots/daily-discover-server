package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.ProductSearchKeyword;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductSearchKeywordMapper extends BaseMapper<ProductSearchKeyword> {
    
    /**
     * 记录搜索关键词
     */
    @Select("INSERT INTO product_search_keywords (keyword, search_count) VALUES (#{keyword}, 1) ON DUPLICATE KEY UPDATE search_count = search_count + 1, updated_at = NOW()")
    void recordSearch(@Param("keyword") String keyword);
    
    /**
     * 查询搜索建议
     */
    @Select("SELECT keyword FROM product_search_keywords WHERE keyword LIKE CONCAT(#{keyword}, '%') ORDER BY search_count DESC LIMIT 10")
    List<String> findSuggestions(@Param("keyword") String keyword);
    
    /**
     * 查询热门搜索关键词
     */
    @Select("SELECT keyword FROM product_search_keywords ORDER BY search_count DESC LIMIT #{limit}")
    List<String> findHotKeywords(@Param("limit") int limit);
    
    /**
     * 根据关键词查询搜索统计
     */
    @Select("SELECT * FROM product_search_keywords WHERE keyword = #{keyword}")
    ProductSearchKeyword findByKeyword(@Param("keyword") String keyword);
}