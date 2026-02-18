package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客户偏好设置模型类
 */
@Data
public class CustomerPreference {
    private Long id;
    private Long customerId;
    private String language;
    private String currency;
    private String timezone;
    private String theme;
    private Boolean emailNotifications;
    private Boolean smsNotifications;
    private Boolean pushNotifications;
    private Boolean marketingEmails;
    private Boolean productRecommendations;
    private Boolean priceAlerts;
    private Boolean orderUpdates;
    private Boolean privacyPublicProfile;
    private Boolean privacyShowEmail;
    private Boolean privacyShowPhone;
    private Boolean privacyShowPurchaseHistory;
    private String preferredCategories;
    private String preferredBrands;
    private String preferredPriceRange;
    private String shoppingPreferences;
    private String communicationPreferences;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}