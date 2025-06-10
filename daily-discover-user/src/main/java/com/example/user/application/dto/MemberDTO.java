package com.example.user.application.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会员DTO
 * 纯数据结构，无业务行为，用于应用层和表现层之间的数据传输
 */
@Data
public class MemberDTO implements Serializable {

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
     * 会员等级
     */
    private Integer memberLevel;

    /**
     * 成长值
     */
    private Integer growthValue;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 已使用积分
     */
    private Integer usedPoints;

    /**
     * 是否永久会员:0-否,1-是
     */
    private Boolean isForever;

    /**
     * 会员开始时间
     */
    private LocalDateTime startTime;

    /**
     * 会员结束时间
     */
    private LocalDateTime endTime;

    /**
     * 状态:0-禁用,1-正常
     */
    private Integer status;

    /**
     * 免邮次数
     */
    private Integer freeShippingCount;

    /**
     * 免退次数
     */
    private Integer freeReturnCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 会员等级名称
     */
    private String levelName;

    /**
     * 会员等级图标
     */
    private String levelIcon;

    /**
     * 会员折扣
     */
    private java.math.BigDecimal discount;

    /**
     * 会员权益
     */
    private String benefits;

    /**
     * 是否有效会员
     */
    private Boolean isValid;

    /**
     * 距离下一等级所需成长值
     */
    private Integer growthToNextLevel;
} 