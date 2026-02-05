package com.dailydiscover.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProductAction {
    private Long id;
    private Long productId;
    private Long userId;
    private String actionType;
    private Integer quantity;
    private String sessionId;
    private String ipAddress;
    private String userAgent;
    private String referrer;
    private LocalDateTime actionDate;
    private String status;
    private String metadata;
    private LocalDateTime createdAt;
}