package com.example.user.domain.service;

import com.example.user.domain.model.id.MemberId;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.member.Member;

/**
 * 会员领域服务接口
 */
public interface MemberDomainService {
    /**
     * 创建会员
     *
     * @param userId 用户ID
     * @return 会员
     */
    Member createMember(UserId userId);

    /**
     * 会员升级
     *
     * @param memberId 会员ID
     * @param growthValue 成长值
     * @return 会员
     */
    Member upgradeMember(MemberId memberId, int growthValue);

    /**
     * 延长会员时间
     *
     * @param memberId 会员ID
     * @param months 延长月数
     * @return 会员
     */
    Member extendMembership(MemberId memberId, int months);

    /**
     * 设置为永久会员
     *
     * @param memberId 会员ID
     * @return 会员
     */
    Member setForeverMembership(MemberId memberId);

    /**
     * 增加积分
     *
     * @param memberId 会员ID
     * @param points 积分
     * @param source 来源
     * @return 会员
     */
    Member addPoints(MemberId memberId, int points, String source);

    /**
     * 使用积分
     *
     * @param memberId 会员ID
     * @param points 积分
     * @param usage 用途
     * @return 是否成功
     */
    boolean usePoints(MemberId memberId, int points, String usage);

    /**
     * 增加成长值
     *
     * @param memberId 会员ID
     * @param growthValue 成长值
     * @param source 来源
     * @return 会员
     */
    Member addGrowthValue(MemberId memberId, int growthValue, String source);

    /**
     * 增加免邮次数
     *
     * @param memberId 会员ID
     * @param count 次数
     * @return 会员
     */
    Member addFreeShippingCount(MemberId memberId, int count);

    /**
     * 使用免邮特权
     *
     * @param memberId 会员ID
     * @return 是否成功
     */
    boolean useFreeShipping(MemberId memberId);

    /**
     * 增加免退次数
     *
     * @param memberId 会员ID
     * @param count 次数
     * @return 会员
     */
    Member addFreeReturnCount(MemberId memberId, int count);

    /**
     * 使用免退特权
     *
     * @param memberId 会员ID
     * @return 是否成功
     */
    boolean useFreeReturn(MemberId memberId);

    /**
     * 禁用会员
     *
     * @param memberId 会员ID
     * @return 是否成功
     */
    boolean disableMember(MemberId memberId);

    /**
     * 启用会员
     *
     * @param memberId 会员ID
     * @return 是否成功
     */
    boolean enableMember(MemberId memberId);
} 