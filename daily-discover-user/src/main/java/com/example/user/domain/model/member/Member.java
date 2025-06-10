package com.example.user.domain.model.member;

import com.example.user.domain.model.id.MemberId;
import com.example.user.domain.model.id.UserId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 会员聚合根
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 会员ID
     */
    private MemberId id;

    /**
     * 用户ID
     */
    private UserId userId;

    /**
     * 会员等级
     */
    private Integer level;

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
     * 是否永久会员
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
     * 状态：0-禁用，1-正常
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
     * 创建会员
     */
    public static Member create(MemberId id, UserId userId) {
        Member member = new Member();
        member.id = id;
        member.userId = userId;
        member.level = 1;
        member.growthValue = 0;
        member.points = 0;
        member.usedPoints = 0;
        member.isForever = false;
        member.startTime = LocalDateTime.now();
        member.endTime = LocalDateTime.now().plusYears(1);
        member.status = 1;
        member.freeShippingCount = 0;
        member.freeReturnCount = 0;
        member.createTime = LocalDateTime.now();
        member.updateTime = LocalDateTime.now();
        return member;
    }

    /**
     * 升级会员
     */
    public void upgrade(int newLevel, int growthValue) {
        if (newLevel > this.level) {
            this.level = newLevel;
            this.growthValue = growthValue;
            this.updateTime = LocalDateTime.now();
        }
    }

    /**
     * 延长会员时间
     */
    public void extend(int months) {
        if (!isForever) {
            if (endTime.isBefore(LocalDateTime.now())) {
                endTime = LocalDateTime.now().plusMonths(months);
            } else {
                endTime = endTime.plusMonths(months);
            }
            updateTime = LocalDateTime.now();
        }
    }

    /**
     * 设置为永久会员
     */
    public void setForever() {
        this.isForever = true;
        this.endTime = null;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 禁用会员
     */
    public void disable() {
        this.status = 0;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 启用会员
     */
    public void enable() {
        this.status = 1;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 增加免邮次数
     */
    public void addFreeShippingCount(int count) {
        if (count > 0) {
            this.freeShippingCount += count;
            this.updateTime = LocalDateTime.now();
        }
    }

    /**
     * 增加免退次数
     */
    public void addFreeReturnCount(int count) {
        if (count > 0) {
            this.freeReturnCount += count;
            this.updateTime = LocalDateTime.now();
        }
    }

    /**
     * 增加积分
     */
    public void addPoints(int points) {
        if (points > 0) {
            this.points += points;
            this.updateTime = LocalDateTime.now();
        }
    }

    /**
     * 使用积分
     */
    public boolean usePoints(int points) {
        if (points <= 0 || this.points < points) {
            return false;
        }
        
        this.points -= points;
        this.usedPoints += points;
        this.updateTime = LocalDateTime.now();
        return true;
    }

    /**
     * 增加成长值
     */
    public void addGrowthValue(int growthValue) {
        if (growthValue > 0) {
            this.growthValue += growthValue;
            this.updateTime = LocalDateTime.now();
        }
    }

    /**
     * 使用免邮特权
     */
    public boolean useFreeShipping() {
        if (this.freeShippingCount <= 0) {
            return false;
        }
        
        this.freeShippingCount--;
        this.updateTime = LocalDateTime.now();
        return true;
    }

    /**
     * 使用免退特权
     */
    public boolean useFreeReturn() {
        if (this.freeReturnCount <= 0) {
            return false;
        }
        
        this.freeReturnCount--;
        this.updateTime = LocalDateTime.now();
        return true;
    }

    /**
     * 是否有效会员
     */
    public boolean isValid() {
        return this.status == 1 && (this.isForever || this.endTime.isAfter(LocalDateTime.now()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
} 