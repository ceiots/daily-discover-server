package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 支付方式表实体类
 */
@Data
@TableName("payment_methods")
public class PaymentMethod {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("method_code")
    private String methodCode;
    
    @TableField("method_name")
    private String methodName;
    
    @TableField("method_type")
    private String methodType;
    
    @TableField("icon_url")
    private String iconUrl;
    
    @TableField("description")
    private String description;
    
    @TableField("is_enabled")
    private Boolean isEnabled;
    
    @TableField("sort_order")
    private Integer sortOrder;
    
    @TableField("config_params")
    private String configParams;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}