package com.example.model;

import java.util.Date;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 商品搜索关键词表实体
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductSearchKeyword {
    private Long id;
    private Long productId;             // 商品ID
    private String keyword;             // 搜索关键词
    private Integer weight;             // 权重
    private Boolean isManual;           // 是否手动添加
    private Date createTime;            // 创建时间
    
    /**
     * 创建搜索关键词
     */
    public static ProductSearchKeyword create(Long productId, String keyword, 
            Integer weight, Boolean isManual) {
        
        ProductSearchKeyword searchKeyword = new ProductSearchKeyword();
        searchKeyword.setProductId(productId);
        searchKeyword.setKeyword(keyword);
        searchKeyword.setWeight(weight != null ? weight : 0);
        searchKeyword.setIsManual(isManual != null ? isManual : false);
        searchKeyword.setCreateTime(new Date());
        
        return searchKeyword;
    }
} 