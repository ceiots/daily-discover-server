package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款记录表实体类
 */
@Data
@TableName("refund_records")
public class RefundRecord {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("payment_transaction_id")
    private Long paymentTransactionId;
    
    @TableField("order_id")
    private Long orderId;
    
    @TableField("refund_no")
    private String refundNo;
    
    @TableField("refund_amount")
    private BigDecimal refundAmount;
    
    @TableField("refund_reason")
    private String refundReason;
    
    @TableField("status")
    private String status;
    
    @TableField("third_party_refund_no")
    private String thirdPartyRefundNo;
    
    @TableField("third_party_status")
    private String thirdPartyStatus;
    
    @TableField("third_party_response")
    private String thirdPartyResponse;
    
    @TableField("refund_request_at")
    private LocalDateTime refundRequestAt;
    
    @TableField("refund_completed_at")
    private LocalDateTime refundCompletedAt;
    
    @TableField("error_code")
    private String errorCode;
    
    @TableField("error_message")
    private String errorMessage;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}