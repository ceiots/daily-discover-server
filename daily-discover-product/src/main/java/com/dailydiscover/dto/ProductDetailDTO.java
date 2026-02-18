package com.dailydiscover.dto;

import com.dailydiscover.model.Product;
import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 产品详情数据传输对象
 * 组合产品和产品属性信息
 */
@Data
public class ProductDetailDTO {
    
    /** 产品基本信息 */
    private Product product;
    
    /** 分类后的商品图片 */
    private Map<String, List<ProductImageDTO>> categorizedImages;
    
    /** 是否热门产品 */
    private Boolean isHot;
    
    /** 是否新品 */
    private Boolean isNew;
    
    /** 是否推荐产品 */
    private Boolean isRecommended;
    
    /** 产品标签 */
    private String tags;
    
    /** 紧急级别 */
    private String urgencyLevel;
    
    /** 热点类型 */
    private String hotspotType;
    
    /** 构造函数 */
    public ProductDetailDTO(Product product) {
        this.product = product;
        // 从产品信息中提取相关属性
        this.isHot = false; // 默认值，需要根据业务逻辑设置
        this.isNew = false; // 默认值，需要根据业务逻辑设置
        this.isRecommended = false; // 默认值，需要根据业务逻辑设置
        this.tags = ""; // 默认值
        this.urgencyLevel = "normal"; // 默认值
        this.hotspotType = "none"; // 默认值
    }
}