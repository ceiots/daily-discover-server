package com.dailydiscover.dto;

import com.dailydiscover.model.Product;
import com.dailydiscover.model.ProductAttribute;
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
    
    /** 产品属性信息 */
    private ProductAttribute attribute;
    
    /** 分类后的商品图片 */
    private Map<String, List<ProductImageDTO>> categorizedImages;
    
    /** 是否热门产品 */
    public Boolean getIsHot() {
        return attribute != null && attribute.getIsHot() != null ? attribute.getIsHot() : false;
    }
    
    /** 是否新品 */
    public Boolean getIsNew() {
        return attribute != null && attribute.getIsNew() != null ? attribute.getIsNew() : false;
    }
    
    /** 是否推荐产品 */
    public Boolean getIsRecommended() {
        return attribute != null && attribute.getIsRecommended() != null ? attribute.getIsRecommended() : false;
    }
    
    /** 获取产品标签 */
    public String getTags() {
        return attribute != null ? attribute.getTags() : null;
    }
    
    /** 获取紧急级别 */
    public String getUrgencyLevel() {
        return attribute != null ? attribute.getUrgencyLevel() : null;
    }
    
    /** 获取热点类型 */
    public String getHotspotType() {
        return attribute != null ? attribute.getHotspotType() : null;
    }
    
    /** 构造函数 */
    public ProductDetailDTO(Product product, ProductAttribute attribute) {
        this.product = product;
        this.attribute = attribute;
    }
    
    /** 空构造函数 */
    public ProductDetailDTO() {
    }
}