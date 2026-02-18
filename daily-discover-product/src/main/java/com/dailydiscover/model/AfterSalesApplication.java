package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 售后申请表
 */
@Data
@TableName("after_sales_applications")
public class AfterSalesApplication {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("application_no")
    private String applicationNo;
    
    @TableField("order_id")
    private Long orderId;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("after_sales_type")
    private String afterSalesType;
    
    @TableField("apply_reason")
    private String applyReason;
    
    @TableField("product_title")
    private String productTitle;
    
    @TableField("purchase_price")
    private BigDecimal purchasePrice;
    
    @TableField("status")
    private String status;
    
    @TableField("processor_id")
    private Long processorId;
    
    @TableField("process_notes")
    private String processNotes;
    
    @TableField("refund_amount")
    private BigDecimal refundAmount;
    
    @TableField("applied_at")
    private LocalDateTime appliedAt;
    
    @TableField("processed_at")
    private LocalDateTime processedAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}