package com.example.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * 订单实体类
 */
@Data
public class Order {
    private Long id;
    private Long userId;
    private String orderNumber;
    private BigDecimal paymentAmount;
    private Integer status;
    private Integer paymentMethod;
    private Date createTime;
    private Date payTime;
    private Date shipTime;
    private Date receiveTime;
    private Date updateTime;
    private String refundReason;
    private Long addressId;
    private Date createdAt;
    private Date paymentTime;
    
    // 非数据库字段
    private List<OrderItem> items;
    private String statusText;
    private String paymentMethodText;
    private BigDecimal totalAmount;
    private String date;
    private String countdown;

}