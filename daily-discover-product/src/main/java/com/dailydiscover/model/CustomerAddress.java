package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客户地址模型类
 */
@Data
public class CustomerAddress {
    private Long id;
    private Long customerId;
    private String addressType;
    private String recipientName;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String street;
    private String detailAddress;
    private String postalCode;
    private Boolean isDefault;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}