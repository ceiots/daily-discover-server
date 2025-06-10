package com.example.user.domain.repository;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 会员查询条件
 */
@Data
@Accessors(chain = true)
public class MemberQueryCondition {
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 手机号
     */
    private String mobile;
    
    /**
     * 会员等级
     */
    private Integer level;
    
    /**
     * 会员等级列表
     */
    private List<Integer> levelList;
    
    /**
     * 最小成长值
     */
    private Integer minGrowthValue;
    
    /**
     * 最大成长值
     */
    private Integer maxGrowthValue;
    
    /**
     * 最小积分
     */
    private Integer minPoints;
    
    /**
     * 最大积分
     */
    private Integer maxPoints;
    
    /**
     * 是否永久会员
     */
    private Boolean isForever;
    
    /**
     * 状态：0-禁用，1-正常
     */
    private Integer status;
    
    /**
     * 状态列表
     */
    private List<Integer> statusList;
    
    /**
     * 会员开始时间范围（开始）
     */
    private LocalDateTime startTimeBegin;
    
    /**
     * 会员开始时间范围（结束）
     */
    private LocalDateTime startTimeEnd;
    
    /**
     * 会员结束时间范围（开始）
     */
    private LocalDateTime endTimeBegin;
    
    /**
     * 会员结束时间范围（结束）
     */
    private LocalDateTime endTimeEnd;
} 