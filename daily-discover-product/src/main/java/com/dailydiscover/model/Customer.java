package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客户模型类
 */
@Data
public class Customer {
    private Long id;
    private Long userId;
    private String customerCode;
    private String name;
    private String email;
    private String phone;
    private String avatar;
    private String gender;
    private LocalDateTime birthday;
    private String membershipLevel;
    private Integer points;
    private String status;
    private LocalDateTime registrationDate;
    private LocalDateTime lastLoginDate;
    private LocalDateTime lastPurchaseDate;
    private Integer totalOrders;
    private Double totalSpent;
    private String tags;
    private String preferences;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}