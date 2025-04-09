package com.example.model;

import lombok.Data;
import java.util.Date;
import java.util.List;

/**
 * 物流信息实体类
 */
@Data
public class LogisticsInfo {
    /**
     * 物流ID
     */
    private Long id;
    
    /**
     * 订单ID
     */
    private Long orderId;
    
    /**
     * 物流单号
     */
    private String trackingNumber;
    
    /**
     * 物流公司编码
     */
    private String companyCode;
    
    /**
     * 物流公司名称
     */
    private String companyName;
    
    /**
     * 物流状态：0-待发货，1-已发货，2-运输中，3-已签收，4-异常
     */
    private Integer status;
    
    /**
     * 物流状态文本
     */
    private String statusText;
    
    /**
     * 发货时间
     */
    private Date shippingTime;
    
    /**
     * 预计送达时间
     */
    private Date estimatedDeliveryTime;
    
    /**
     * 实际送达时间
     */
    private Date actualDeliveryTime;
    
    /**
     * 物流轨迹列表
     */
    private List<LogisticsTrack> tracks;
    
    /**
     * 收件人信息
     */
    private String receiverName;
    
    /**
     * 收件人电话
     */
    private String receiverPhone;
    
    /**
     * 收件人地址
     */
    private String receiverAddress;
} 