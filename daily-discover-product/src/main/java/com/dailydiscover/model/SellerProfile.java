package com.dailydiscover.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SellerProfile {
    private Long id;
    private Long sellerId;
    private BigDecimal positiveFeedback;
    private String contactInfo;
    private String services;
    private String certifications;
    private String businessHours;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}