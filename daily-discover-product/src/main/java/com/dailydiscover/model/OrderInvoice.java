package com.dailydiscover.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单发票表
 */
@Data
@TableName("order_invoices")
public class OrderInvoice {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("order_id")
    private Long orderId;
    
    @TableField("invoice_type")
    private String invoiceType;
    
    @TableField("invoice_title")
    private String invoiceTitle;
    
    @TableField("tax_number")
    private String taxNumber;
    
    @TableField("company_address")
    private String companyAddress;
    
    @TableField("company_phone")
    private String companyPhone;
    
    @TableField("bank_name")
    private String bankName;
    
    @TableField("bank_account")
    private String bankAccount;
    
    @TableField("invoice_content")
    private String invoiceContent;
    
    @TableField("invoice_amount")
    private BigDecimal invoiceAmount;
    
    @TableField("invoice_status")
    private String invoiceStatus;
    
    @TableField("issued_at")
    private LocalDateTime issuedAt;
    
    @TableField("invoice_no")
    private String invoiceNo;
    
    @TableField("invoice_number")
    private String invoiceNumber;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}