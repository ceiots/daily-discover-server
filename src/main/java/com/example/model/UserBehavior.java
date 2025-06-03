package com.example.model;

import java.util.Date;

import lombok.Data;

/**
 * 用户行为实体类
 * 用于收集更全面的用户行为数据，包括：
 * 1. 浏览行为
 * 2. 点击行为
 * 3. 停留时间
 * 4. 购买行为
 * 5. 收藏行为
 * 6. 评价行为
 */
@Data
public class UserBehavior {
    private Long id;
    private Long userId;
    private Long productId;
    private Long categoryId;
    
    /**
     * 行为类型:
     * VIEW - 浏览
     * CLICK - 点击
     * STAY - 停留
     * BUY - 购买
     * FAVORITE - 收藏
     * COMMENT - 评价
     * SHARE - 分享
     */
    private String behaviorType;
    
    /**
     * 行为时间
     */
    private Date behaviorTime;
    
    /**
     * 持续时间(秒)
     */
    private Double duration;
    
    /**
     * 额外数据(JSON)
     * 可以存储与行为相关的其他信息，如：
     * - 点击的具体位置
     * - 评价的星级和内容
     * - 购买的数量和金额
     * - 分享的平台
     */
    private String extraData;
    
    /**
     * 设备信息
     */
    private String deviceInfo;
    
    /**
     * 用户IP地址
     */
    private String ipAddress;
    
    /**
     * 行为得分
     * 用于计算用户对某类商品的兴趣度
     * 不同行为有不同的权重：
     * - 浏览: 1分
     * - 点击: 2分
     * - 停留(每10秒): 0.5分
     * - 收藏: 5分
     * - 评价: 5分
     * - 购买: 10分
     * - 分享: 8分
     */
    private Double behaviorScore;
    
    private Date createdAt;
    private Date updatedAt;
} 