package com.example.model;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 商品价格管理实体类
 * 提取自Product表，用于管理商品定价策略
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductPrice {
    private Long id;
    private Long productId;
    private BigDecimal price;             // 商品基础价格
    private BigDecimal originalPrice;     // 商品原价/市场价，用于显示折扣
    private BigDecimal promotionPrice;    // 商品促销价格
    private Date promotionStartTime;      // 促销开始时间
    private Date promotionEndTime;        // 促销结束时间
    private Boolean isActive;             // 是否激活该价格方案
    private String priceName;             // 价格方案名称，如"双11促销"、"会员价"等
    private Integer priceType;            // 价格类型：1-普通价格，2-促销价格，3-会员价格，4-批发价格
    private String priceDescription;      // 价格描述
    private Date createTime;
    private Date updateTime;
    
    /**
     * 检查促销是否有效
     * @return 如果当前时间在促销有效期内则返回true
     */
    public boolean isPromotionActive() {
        if (promotionPrice == null || promotionStartTime == null || promotionEndTime == null) {
            return false;
        }
        
        Date now = new Date();
        return now.after(promotionStartTime) && now.before(promotionEndTime);
    }
    
    /**
     * 获取当前实际价格，考虑促销情况
     * @return 当前有效价格
     */
    public BigDecimal getCurrentPrice() {
        if (isPromotionActive()) {
            return promotionPrice;
        }
        return price;
    }
    
    /**
     * 计算折扣百分比
     * @return 折扣百分比，例如: 85 表示 85折 (15%的折扣)
     */
    public int getDiscountPercentage() {
        BigDecimal currentPrice = getCurrentPrice();
        if (currentPrice == null || originalPrice == null || originalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return 100;
        }
        
        return currentPrice.multiply(new BigDecimal(100)).divide(originalPrice, 0, BigDecimal.ROUND_HALF_UP).intValue();
    }
} 