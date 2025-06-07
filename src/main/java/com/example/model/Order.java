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
   
    private BigDecimal platformCommissionAmount; // 平台佣金金额
    private BigDecimal shopAmount; // 店铺实际收款金额
    private Integer settlementStatus; // 结算状态：0-未结算，1-已结算
    private Date settlementTime; // 结算时间
    
    // 非数据库字段
    private List<OrderItem> items;
    private String statusText;
    private String paymentMethodText;
    private BigDecimal totalAmount;
    private String date;
    private String countdown;

}