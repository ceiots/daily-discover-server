package com.example.model;

import lombok.Data;

/**
 * 物流公司实体类
 */
@Data
public class LogisticsCompany {
    /**
     * 物流公司ID
     */
    private Long id;
    
    /**
     * 物流公司编码
     */
    private String code;
    
    /**
     * 物流公司名称
     */
    private String name;
    
    /**
     * 物流公司简称
     */
    private String shortName;
    
    /**
     * 物流公司电话
     */
    private String phone;
    
    /**
     * 物流公司网址
     */
    private String website;
    
    /**
     * 物流公司Logo
     */
    private String logo;
    
    /**
     * 是否启用
     */
    private Boolean enabled;
    
    /**
     * 排序
     */
    private Integer sort;
    
    /**
     * 备注
     */
    private String remark;
} 