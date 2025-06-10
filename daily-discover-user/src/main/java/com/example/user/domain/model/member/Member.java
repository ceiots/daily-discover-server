package com.example.user.domain.model.member;

import com.example.common.exception.BusinessException;
import com.example.user.domain.model.id.MemberId;
import com.example.user.domain.model.id.UserId;
import com.example.user.infrastructure.common.result.ResultCode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
     * 创建会员
     *
     * @param userId 用户ID
     * @param level 等级
     * @param isForever 是否永久
     * @param months 月数
     * @return 会员
     */
    public static Member create(UserId userId, Integer level, Boolean isForever, Integer months) {
        Member member = new Member();
        member.userId = userId;
        member.memberLevel = level;
        member.growthValue = 0;
        member.points = 0;
        member.usedPoints = 0;
        member.isForever = isForever;
        member.startTime = LocalDateTime.now();
        
        if (!isForever && months != null && months > 0) {
            member.endTime = member.startTime.plusMonths(months);
        }
        
        member.status = 1; // 正常
        member.freeShippingCount = 0;
        member.freeReturnCount = 0;
        member.createTime = LocalDateTime.now();
        member.updateTime = LocalDateTime.now();
        return member;
    }

    /**
     * 设置ID
     *
     * @param id ID
     */
    public void setId(MemberId id) {
        this.id = id;
    }

    /**
     * 升级会员
     *
     * @param level 等级
     * @param growthValue 成长值
     */
    public void upgrade(Integer level, Integer growthValue) {
        if (level <= this.memberLevel) {
            throw new BusinessException(ResultCode.MEMBER_LEVEL_INVALID);
        }
        
        this.memberLevel = level;
        
        if (growthValue != null && growthValue > 0) {
            this.growthValue += growthValue;
        }
        
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 延长会员有效期
     *
     * @param months 月数
     */
    public void extend(Integer months) {
        if (this.isForever) {
            return;
        }
        
        if (months == null || months <= 0) {
            throw new BusinessException(ResultCode.INVALID_MONTHS);
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        if (this.endTime == null || this.endTime.isBefore(now)) {
            this.endTime = now.plusMonths(months);
        } else {
            this.endTime = this.endTime.plusMonths(months);
        }
        
        this.updateTime = now;
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
     * 增加成长值
     *
     * @param growthValue 成长值
     */
    public void addGrowthValue(Integer growthValue) {
        if (growthValue == null || growthValue <= 0) {
            throw new BusinessException(ResultCode.INVALID_GROWTH_VALUE);
        }
        
        this.growthValue += growthValue;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 增加积分
     *
     * @param points 积分
     */
    public void addPoints(Integer points) {
        if (points == null || points <= 0) {
            throw new BusinessException(ResultCode.INVALID_POINTS);
        }
        
        this.points += points;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 使用积分
     *
     * @param points 积分
     */
    public void usePoints(Integer points) {
        if (points == null || points <= 0) {
            throw new BusinessException(ResultCode.INVALID_POINTS);
        }
        
        if (this.points < points) {
            throw new BusinessException(ResultCode.POINTS_NOT_ENOUGH);
        }
        
        this.points -= points;
        this.usedPoints += points;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 使用免邮次数
     */
    public void useFreeShipping() {
        if (this.freeShippingCount <= 0) {
            throw new BusinessException(ResultCode.FREE_SHIPPING_NOT_ENOUGH);
        }
        
        this.freeShippingCount--;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 使用免退次数
     */
    public void useFreeReturn() {
        if (this.freeReturnCount <= 0) {
            throw new BusinessException(ResultCode.FREE_RETURN_NOT_ENOUGH);
        }
        
        this.freeReturnCount--;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 增加免邮次数
     *
     * @param count 次数
     */
    public void addFreeShippingCount(Integer count) {
        if (count == null || count <= 0) {
            throw new BusinessException(ResultCode.INVALID_COUNT);
        }
        
        this.freeShippingCount += count;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 增加免退次数
     *
     * @param count 次数
     */
    public void addFreeReturnCount(Integer count) {
        if (count == null || count <= 0) {
            throw new BusinessException(ResultCode.INVALID_COUNT);
        }
        
        this.freeReturnCount += count;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 是否有效会员
     *
     * @return 是否有效
     */
    public boolean isValid() {
        if (this.status != 1) {
            return false;
        }
        
        if (this.isForever) {
            return true;
        }
        
        return this.endTime != null && this.endTime.isAfter(LocalDateTime.now());
    }

    /**
     * 是否可用积分
     *
     * @param points 积分
     * @return 是否可用
     */
    public boolean canUsePoints(Integer points) {
        return this.status == 1 && this.points >= points;
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