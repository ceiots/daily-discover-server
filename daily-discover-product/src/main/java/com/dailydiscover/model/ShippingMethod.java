package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 运输方式模型类
 */
@Data
public class ShippingMethod {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String carrier;
    private String serviceType;
    private Double baseCost;
    private Double weightLimit;
    private Double lengthLimit;
    private Double widthLimit;
    private Double heightLimit;
    private Integer estimatedDaysMin;
    private Integer estimatedDaysMax;
    private Boolean isInternational;
    private Boolean isEnabled;
    private String regions;
    private String restrictions;
    private String trackingUrl;
    private Double handlingFee;
    private Double insuranceFee;
    private String calculationMethod;
    private Integer priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}