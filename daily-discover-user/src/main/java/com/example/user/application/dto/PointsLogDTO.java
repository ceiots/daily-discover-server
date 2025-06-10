package com.example.user.application.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积分记录DTO
 */
@Data
public class PointsLogDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 类型:1-获取,2-消费,3-过期,4-调整
     */
    private Integer type;

    /**
     * 积分数量
     */
    private Integer points;

    /**
     * 变动前积分
     */
    private Integer beforePoints;

    /**
     * 变动后积分
     */
    private Integer afterPoints;

    /**
     * 来源:1-订单,2-签到,3-活动,4-邀请,5-系统
     */
    private Integer source;

    /**
     * 来源ID
     */
    private String sourceId;

    /**
     * 描述
     */
    private String description;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
} 