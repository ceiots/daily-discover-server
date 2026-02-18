package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客户统计模型类
 */
@Data
public class CustomerStats {
    private Long customerId;
    private Integer totalOrders;
    private Double totalSpent;
    private Double averageOrderValue;
    private Integer favoriteProducts;
    private Integer wishlistItems;
    private Integer reviewsWritten;
    private Integer questionsAsked;
    private Integer returnsRequested;
    private Integer complaintsFiled;
    private Integer referralsMade;
    private Integer pointsEarned;
    private Integer pointsRedeemed;
    private Integer couponsUsed;
    private Integer loyaltyLevel;
    private String customerTier;
    private Double satisfactionScore;
    private Integer daysSinceLastPurchase;
    private Integer daysSinceLastLogin;
    private Integer purchaseFrequency;
    private Double customerLifetimeValue;
    private String customerCategory;
    private LocalDateTime lastUpdatedAt;
}