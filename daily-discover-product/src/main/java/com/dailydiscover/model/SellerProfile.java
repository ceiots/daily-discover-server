package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商家资料表
 */
@Data
@TableName("seller_profiles")
public class SellerProfile {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("seller_id")
    private Long sellerId;
    
    @TableField("positive_feedback")
    private BigDecimal positiveFeedback;
    
    @TableField("contact_info")
    private String contactInfo;
    
    @TableField("services")
    private String services;
    
    @TableField("certifications")
    private String certifications;
    
    @TableField("business_hours")
    private String businessHours;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}