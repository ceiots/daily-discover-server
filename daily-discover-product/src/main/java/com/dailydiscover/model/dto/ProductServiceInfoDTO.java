package com.dailydiscover.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 产品服务信息DTO
 * 用于存储产品参数、售后服务、认证信息等可扩展信息
 */
@Data
public class ProductServiceInfoDTO {
    
    /** 信息项ID */
    private Long infoId;
    
    /** 信息项键（英文标识） */
    private String infoKey;
    
    /** 信息项标签（显示名称） */
    private String infoLabel;
    
    /** 数据类型 */
    private String dataType;
    
    /** 数值单位 */
    private String valueUnit;
    
    // 值存储字段（根据数据类型使用不同的字段）
    
    /** 字符串值 */
    private String stringValue;
    
    /** 数值 */
    private BigDecimal numberValue;
    
    /** 布尔值 */
    private Boolean booleanValue;
    
    /** 日期值 */
    private LocalDate dateValue;
    
    /** 数组值（JSON格式） */
    private String arrayValues;
    
    /** 排序顺序 */
    private Integer sortOrder;
    
    /** 分类信息 */
    private ServiceCategoryDTO category;
    
    /**
     * 获取显示值
     */
    public String getDisplayValue() {
        if (stringValue != null) {
            return stringValue;
        } else if (numberValue != null) {
            return valueUnit != null ? numberValue + " " + valueUnit : numberValue.toString();
        } else if (booleanValue != null) {
            return booleanValue ? "是" : "否";
        } else if (dateValue != null) {
            return dateValue.toString();
        } else if (arrayValues != null) {
            return arrayValues; // 可以进一步解析JSON数组
        }
        return "";
    }
    
    /**
     * 服务分类DTO
     */
    @Data
    public static class ServiceCategoryDTO {
        
        /** 分类ID */
        private Long categoryId;
        
        /** 分类名称 */
        private String categoryName;
        
        /** 分类代码 */
        private String categoryCode;
        
        /** 排序顺序 */
        private Integer sortOrder;
        
        /** 是否可折叠 */
        private Boolean isCollapsible;
        
        /** 显示图标 */
        private String displayIcon;
        
        /** 显示颜色 */
        private String displayColor;
    }
}