package com.example.user.domain.event;

import com.example.user.domain.model.id.UserId;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 积分变更事件
 * 在用户积分发生变化时触发，用于执行后续的业务逻辑，如发送积分变更通知、触发积分兑换等
 */
@Getter
public class PointsChangedEvent extends DomainEvent {

    /**
     * 用户ID
     */
    private final UserId userId;

    /**
     * 变更前积分
     */
    private final Integer beforePoints;

    /**
     * 变更后积分
     */
    private final Integer afterPoints;

    /**
     * 变更积分数量（正数为增加，负数为减少）
     */
    private final Integer changeAmount;

    /**
     * 变更类型：1-获取积分，2-使用积分，3-积分过期，4-积分调整
     */
    private final Integer changeType;

    /**
     * 积分来源：1-订单消费，2-签到，3-活动奖励，4-邀请好友，5-系统赠送
     */
    private final Integer source;

    /**
     * 来源ID
     */
    private final String sourceId;

    /**
     * 变更描述
     */
    private final String description;

    /**
     * 变更时间
     */
    private final LocalDateTime changeTime;

    /**
     * 构造函数
     *
     * @param userId       用户ID
     * @param beforePoints 变更前积分
     * @param afterPoints  变更后积分
     * @param changeAmount 变更积分数量
     * @param changeType   变更类型
     * @param source       积分来源
     * @param sourceId     来源ID
     * @param description  变更描述
     */
    public PointsChangedEvent(
            UserId userId,
            Integer beforePoints,
            Integer afterPoints,
            Integer changeAmount,
            Integer changeType,
            Integer source,
            String sourceId,
            String description
    ) {
        super();
        this.userId = userId;
        this.beforePoints = beforePoints;
        this.afterPoints = afterPoints;
        this.changeAmount = changeAmount;
        this.changeType = changeType;
        this.source = source;
        this.sourceId = sourceId;
        this.description = description;
        this.changeTime = LocalDateTime.now();
    }

    @Override
    public String getEventType() {
        return "POINTS_CHANGED";
    }
}
