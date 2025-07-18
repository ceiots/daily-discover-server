package com.example.user.domain.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会员创建事件
 */
@Getter
@NoArgsConstructor
public class MemberCreatedEvent extends DomainEvent {

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 用户ID - 会员关联的用户账号，用于后续会员相关的通知、积分变更等操作
     */
    private Long userId;

    /**
     * 会员等级ID
     */
    private Long levelId;

    /**
     * 会员等级名称
     */
    private String levelName;

    /**
     * 初始积分
     */
    private Integer initialPoints;

    /**
     * 会员创建时间
     */
    private LocalDateTime createTime;

    /**
     * 构造函数
     *
     * @param memberId      会员ID
     * @param userId        用户ID
     * @param levelId       会员等级ID
     * @param levelName     会员等级名称
     * @param initialPoints 初始积分
     */
    public MemberCreatedEvent(Long memberId, Long userId, Long levelId, String levelName, Integer initialPoints) {
        super();
        this.memberId = memberId;
        this.userId = userId;
        this.levelId = levelId;
        this.levelName = levelName;
        this.initialPoints = initialPoints;
        this.createTime = LocalDateTime.now();
    }

    @Override
    public String getEventType() {
        return "MEMBER_CREATED";
    }
} 