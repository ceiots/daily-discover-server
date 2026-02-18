package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录表实体类
 */
@Data
@TableName("payment_transactions")
public class PaymentTransaction {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("order_id")
    private Long orderId;
    
    @TableField("payment_method_id")
    private Long paymentMethodId;
    
    @TableField("transaction_no")
    private String transactionNo;
    
    @TableField("amount")
    private BigDecimal amount;
    
    @TableField("currency")
    private String currency;
    
    @TableField("status")
    private String status;
    
    @TableField("third_party_transaction_no")
    private String thirdPartyTransactionNo;
    
    @TableField("third_party_status")
    private String thirdPartyStatus;
    
    @TableField("third_party_response")
    private String thirdPartyResponse;
    
    @TableField("payment_request_at")
    private LocalDateTime paymentRequestAt;
    
    @TableField("payment_completed_at")
    private LocalDateTime paymentCompletedAt;
    
    @TableField("payment_expired_at")
    private LocalDateTime paymentExpiredAt;
    
    @TableField("error_code")
    private String errorCode;
    
    @TableField("error_message")
    private String errorMessage;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}