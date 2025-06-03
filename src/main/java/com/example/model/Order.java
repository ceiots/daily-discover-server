package com.example.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
@Data
public class Order {
    private Long id;
    private Long userId;           // 对应数据库 user_id
    private String orderNumber;    // 对应数据库 order_number
    private Date createdAt;        // 对应数据库 created_at
    private Integer paymentMethod;  // 对应数据库 payment_method
    private BigDecimal paymentAmount; // 对应数据库 payment_amount
    private Date paymentTime;      // 对应数据库 payment_time
    private Long addressId;        // 对应数据库 address_id
    private Integer status;        // 对应数据库 status
    private String shippingAddress; // 对应数据库 shipping_address
    private List<OrderItem> items;
    
    private String statusText;
    private String paymentMethodText;
    private String date;
    private String countdown;
    private BigDecimal totalAmount;
    private BigDecimal shipping = new BigDecimal(0);
    private BigDecimal discount = new BigDecimal(0);

}