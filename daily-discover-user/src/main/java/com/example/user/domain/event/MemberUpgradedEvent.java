package com.example.user.domain.event;

import com.example.user.domain.model.id.MemberId;
import com.example.user.domain.model.id.UserId;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 会员升级事件
 * 在会员等级提升时触发，用于执行后续的业务逻辑，如发送会员升级通知、更新用户权益等
 */
@Getter
public class MemberUpgradedEvent extends DomainEvent {

    /**
     * 会员ID
     */
    private final MemberId memberId;

    /**
     * 用户ID
     */
    private final UserId userId;

    /**
     * 原会员等级
     */
    private final Integer previousLevel;

    /**
     * 新会员等级
     */
    private final Integer newLevel;

    /**
     * 升级原因
     */
    private final String upgradeReason;

    /**
     * 升级时间
     */
    private final LocalDateTime upgradeTime;

    /**
     * 构造函数
     *
     * @param memberId      会员ID
     * @param userId        用户ID
     * @param previousLevel 原会员等级
     * @param newLevel      新会员等级
     * @param upgradeReason 升级原因
     */
    public MemberUpgradedEvent(MemberId memberId, UserId userId, Integer previousLevel, Integer newLevel, String upgradeReason) {
        super();
        this.memberId = memberId;
        this.userId = userId;
        this.previousLevel = previousLevel;
        this.newLevel = newLevel;
        this.upgradeReason = upgradeReason;
        this.upgradeTime = LocalDateTime.now();
    }

    @Override
    public String getEventType() {
        return "MEMBER_UPGRADED";
    }
} 