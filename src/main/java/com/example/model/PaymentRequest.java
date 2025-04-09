package com.example.model;

import java.math.BigDecimal;
import java.util.List;

public class PaymentRequest {
    private Long userId;
    private List<Long> itemIds;
    private String paymentMethod;
    
    // 新增属性
    private String payType;
    private String orderNo;
    private BigDecimal totalAmount;
    private String subject;
    private String clientIp;
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getItemIds() {
        return itemIds;
    }

    public void setItemIds(List<Long> itemIds) {
        this.itemIds = itemIds;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    @Override
    public String toString() {
        return "PaymentRequest [userId=" + userId + ", itemIds=" + itemIds + ", paymentMethod=" + paymentMethod
                + ", payType=" + payType + ", orderNo=" + orderNo + ", totalAmount=" + totalAmount + ", subject="
                + subject + ", clientIp=" + clientIp + "]";
    }

    
}