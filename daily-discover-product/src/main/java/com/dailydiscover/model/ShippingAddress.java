package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 物流地址模型类
 */
@Data
public class ShippingAddress {
    private Long id;
    private Long customerId;
    private String addressType;
    private String recipientName;
    private String phone;
    private String email;
    private String company;
    private String country;
    private String province;
    private String city;
    private String district;
    private String street;
    private String detailAddress;
    private String postalCode;
    private Double latitude;
    private Double longitude;
    private Boolean isDefault;
    private Boolean isVerified;
    private String verificationStatus;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}