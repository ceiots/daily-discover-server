package com.example.user.domain.model.user;

import com.example.user.domain.model.id.UserId;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用户积分记录实体
 */
@Getter
@Setter
public class UserPointsLog {

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
     * @param userId 用户ID
     * @param points 积分
     * @param beforePoints 变动前积分
     * @param afterPoints 变动后积分
     * @param source 来源
     * @param sourceId 来源ID
     * @param description 描述
     * @return 积分记录
     */
    public static UserPointsLog createGainLog(UserId userId, Integer points, Integer beforePoints, Integer afterPoints, 
                                             Integer source, String sourceId, String description) {
        UserPointsLog log = new UserPointsLog();
        log.setUserId(userId);
        log.setType(1); // 获取
        log.setPoints(points);
        log.setBeforePoints(beforePoints);
        log.setAfterPoints(afterPoints);
        log.setSource(source);
        log.setSourceId(sourceId);
        log.setDescription(description);
        log.setCreateTime(LocalDateTime.now());
        return log;
    }

    /**
     * 创建消费积分记录
     *
     * @param userId 用户ID
     * @param points 积分
     * @param beforePoints 变动前积分
     * @param afterPoints 变动后积分
     * @param source 来源
     * @param sourceId 来源ID
     * @param description 描述
     * @return 积分记录
     */
    public static UserPointsLog createUseLog(UserId userId, Integer points, Integer beforePoints, Integer afterPoints, 
                                           Integer source, String sourceId, String description) {
        UserPointsLog log = new UserPointsLog();
        log.setUserId(userId);
        log.setType(2); // 消费
        log.setPoints(points);
        log.setBeforePoints(beforePoints);
        log.setAfterPoints(afterPoints);
        log.setSource(source);
        log.setSourceId(sourceId);
        log.setDescription(description);
        log.setCreateTime(LocalDateTime.now());
        return log;
    }

    /**
     * 创建积分过期记录
     *
     * @param userId 用户ID
     * @param points 积分
     * @param beforePoints 变动前积分
     * @param afterPoints 变动后积分
     * @param description 描述
     * @return 积分记录
     */
    public static UserPointsLog createExpireLog(UserId userId, Integer points, Integer beforePoints, Integer afterPoints, String description) {
        UserPointsLog log = new UserPointsLog();
        log.setUserId(userId);
        log.setType(3); // 过期
        log.setPoints(points);
        log.setBeforePoints(beforePoints);
        log.setAfterPoints(afterPoints);
        log.setSource(5); // 系统
        log.setDescription(description);
        log.setCreateTime(LocalDateTime.now());
        return log;
    }

    /**
     * 创建积分调整记录
     *
     * @param userId 用户ID
     * @param points 积分
     * @param beforePoints 变动前积分
     * @param afterPoints 变动后积分
     * @param description 描述
     * @return 积分记录
     */
    public static UserPointsLog createAdjustLog(UserId userId, Integer points, Integer beforePoints, Integer afterPoints, String description) {
        UserPointsLog log = new UserPointsLog();
        log.setUserId(userId);
        log.setType(4); // 调整
        log.setPoints(points);
        log.setBeforePoints(beforePoints);
        log.setAfterPoints(afterPoints);
        log.setSource(5); // 系统
        log.setDescription(description);
        log.setCreateTime(LocalDateTime.now());
        return log;
    }
} 