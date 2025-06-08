package com.example.model;

import java.util.Date;
import java.util.List;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 商品规格信息实体
 * 存储商品的规格参数和属性，包括销售和非销售属性
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductSpecification {
    private Long id;
    private Long productId;
    private List<Specification> specifications;     // 销售规格(SKU区分项)
    private List<ProductAttribute> attributes;      // 非销售属性
    private Boolean hasMultipleSkus;                // 是否多规格商品
    private Date createTime;
    private Date updateTime;
    
    /**
     * 检查是否包含指定名称的销售属性
     */
    public boolean hasSalesAttribute(String name) {
        if (specifications == null || name == null) {
            return false;
        }
        
        for (Specification spec : specifications) {
            if (name.equals(spec.getName())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取指定名称的非销售属性值
     */
    public String getAttributeValue(String name) {
        if (attributes == null || name == null) {
            return null;
        }
        
        for (ProductAttribute attr : attributes) {
            if (name.equals(attr.getName())) {
                return attr.getValue();
            }
        }
        return null;
    }
} 