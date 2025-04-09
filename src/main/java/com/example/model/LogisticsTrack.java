package com.example.model;

import lombok.Data;
import java.util.Date;

/**
 * 物流轨迹实体类
 */
@Data
public class LogisticsTrack {
    /**
     * 轨迹ID
     */
    private Long id;
    
    /**
     * 物流信息ID
     */
    private Long logisticsId;
    
    /**
     * 轨迹时间
     */
    private Date trackTime;
    
    /**
     * 轨迹地点
     */
    private String location;
    
    /**
     * 轨迹描述
     */
    private String description;
    
    /**
     * 轨迹状态
     */
    private String status;
    
    /**
     * 轨迹状态码
     */
    private String statusCode;
    
    /**
     * 操作人
     */
    private String operator;
    
    /**
     * 操作人电话
     */
    private String operatorPhone;
} 