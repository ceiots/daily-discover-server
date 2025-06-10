package com.example.user.domain.model;

import com.example.user.domain.model.id.UserId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户积分记录实体
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPointsLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private UserId userId;

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

    /**
     * 创建获取积分记录
     *
     * @param userId       用户ID
     * @param points       积分
     * @param beforePoints 变动前积分
     * @param afterPoints  变动后积分
     * @param source       来源
     * @param sourceId     来源ID
     * @param description  描述
     * @return 积分记录
     */
    public static UserPointsLog createGainLog(UserId userId, Integer points, Integer beforePoints, Integer afterPoints,
                                            Integer source, String sourceId, String description) {
        UserPointsLog log = new UserPointsLog();
        log.userId = userId;
        log.type = 1; // 获取
        log.points = points;
        log.beforePoints = beforePoints;
        log.afterPoints = afterPoints;
        log.source = source;
        log.sourceId = sourceId;
        log.description = description;
        log.createTime = LocalDateTime.now();
        return log;
    }

    /**
     * 创建消费积分记录
     *
     * @param userId       用户ID
     * @param points       积分
     * @param beforePoints 变动前积分
     * @param afterPoints  变动后积分
     * @param source       来源
     * @param sourceId     来源ID
     * @param description  描述
     * @return 积分记录
     */
    public static UserPointsLog createUseLog(UserId userId, Integer points, Integer beforePoints, Integer afterPoints,
                                           Integer source, String sourceId, String description) {
        UserPointsLog log = new UserPointsLog();
        log.userId = userId;
        log.type = 2; // 消费
        log.points = points;
        log.beforePoints = beforePoints;
        log.afterPoints = afterPoints;
        log.source = source;
        log.sourceId = sourceId;
        log.description = description;
        log.createTime = LocalDateTime.now();
        return log;
    }

    /**
     * 创建过期积分记录
     *
     * @param userId       用户ID
     * @param points       积分
     * @param beforePoints 变动前积分
     * @param afterPoints  变动后积分
     * @param description  描述
     * @return 积分记录
     */
    public static UserPointsLog createExpireLog(UserId userId, Integer points, Integer beforePoints, Integer afterPoints,
                                              String description) {
        UserPointsLog log = new UserPointsLog();
        log.userId = userId;
        log.type = 3; // 过期
        log.points = points;
        log.beforePoints = beforePoints;
        log.afterPoints = afterPoints;
        log.source = 5; // 系统
        log.description = description;
        log.createTime = LocalDateTime.now();
        return log;
    }

    /**
     * 创建调整积分记录
     *
     * @param userId       用户ID
     * @param points       积分
     * @param beforePoints 变动前积分
     * @param afterPoints  变动后积分
     * @param description  描述
     * @return 积分记录
     */
    public static UserPointsLog createAdjustLog(UserId userId, Integer points, Integer beforePoints, Integer afterPoints,
                                              String description) {
        UserPointsLog log = new UserPointsLog();
        log.userId = userId;
        log.type = 4; // 调整
        log.points = points;
        log.beforePoints = beforePoints;
        log.afterPoints = afterPoints;
        log.source = 5; // 系统
        log.description = description;
        log.createTime = LocalDateTime.now();
        return log;
    }
    
    /**
     * 设置ID
     *
     * @param id ID
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * 设置过期时间
     *
     * @param expireTime 过期时间
     */
    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }
} 