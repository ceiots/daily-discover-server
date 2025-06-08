package com.example.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 商品促销实体类
 * 用于管理商品的各种促销活动
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductPromotion {
    private Long id;
    private Long productId;
    private Long skuId;                  // SKU ID，NULL表示适用所有SKU
    private BigDecimal promotionPrice;   // 促销价格
    private String promotionTitle;       // 促销标题
    private String promotionDesc;        // 促销描述
    private Date startTime;              // 开始时间
    private Date endTime;                // 结束时间
    private Integer status;              // 促销状态:0-未开始,1-进行中,2-已结束
    private Integer promotionType;       // 促销类型:1-直降,2-折扣,3-限时,4-满减
    private Map<String, Object> promotionRule; // 促销规则，如满减阈值
    private Date createTime;
    private Date updateTime;
    
    /**
     * 检查促销是否有效
     * @return 如果当前时间在促销有效期内则返回true
     */
    public boolean isPromotionActive() {
        if (startTime == null || endTime == null) {
            return false;
        }
        
        Date now = new Date();
        return now.after(startTime) && now.before(endTime);
    }
    
    /**
     * 计算当前促销的实际价格
     * @param originalPrice 原价
     * @return 促销后的价格
     */
    public BigDecimal calculatePromotionPrice(BigDecimal originalPrice) {
        if (!isPromotionActive() || originalPrice == null) {
            return originalPrice;
        }
        
        if (promotionPrice != null) {
            return promotionPrice;
        }
        
        // 根据促销类型计算价格
        switch (promotionType) {
            case 1: // 直降
                return promotionPrice;
            case 2: // 折扣
                if (promotionRule != null && promotionRule.containsKey("discountRate")) {
                    BigDecimal discountRate = new BigDecimal(promotionRule.get("discountRate").toString());
                    return originalPrice.multiply(discountRate).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
                }
                break;
            case 3: // 限时
                return promotionPrice;
            case 4: // 满减
                if (promotionRule != null && 
                    promotionRule.containsKey("threshold") && 
                    promotionRule.containsKey("discountAmount")) {
                    BigDecimal threshold = new BigDecimal(promotionRule.get("threshold").toString());
                    if (originalPrice.compareTo(threshold) >= 0) {
                        BigDecimal discountAmount = new BigDecimal(promotionRule.get("discountAmount").toString());
                        return originalPrice.subtract(discountAmount);
                    }
                }
                break;
        }
        
        return originalPrice;
    }
} 