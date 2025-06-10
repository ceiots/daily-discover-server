package com.example.user.domain.repository;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.id.MemberId;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.member.Member;
import com.example.user.domain.model.member.MemberLevel;
import com.example.user.domain.model.user.UserPointsLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 会员仓储接口
 */
public interface MemberRepository {
    /**
     * 根据ID查询会员
     *
     * @param id 会员ID
     * @return 会员信息
     */
    Optional<Member> findById(MemberId id);

    /**
     * 根据用户ID查询会员
     *
     * @param userId 用户ID
     * @return 会员信息
     */
    Optional<Member> findByUserId(UserId userId);

    /**
     * 根据会员等级查询会员列表
     *
     * @param memberLevel 会员等级
     * @return 会员列表
     */
    List<Member> findByMemberLevel(Integer memberLevel);

    /**
     * 根据状态查询会员列表
     *
     * @param status 状态
     * @return 会员列表
     */
    List<Member> findByStatus(Integer status);

    /**
     * 查询已过期的会员
     *
     * @param expiryDate 过期时间
     * @return 会员列表
     */
    List<Member> findExpiredMembers(LocalDateTime expiryDate);

    /**
     * 分页查询会员
     *
     * @param pageRequest 分页请求
     * @param status 状态
     * @param memberLevel 会员等级
     * @return 会员分页结果
     */
    PageResult<Member> findPage(PageRequest pageRequest, Integer status, Integer memberLevel);

    /**
     * 保存会员
     *
     * @param member 会员
     * @return 保存后的会员
     */
    Member save(Member member);

    /**
     * 更新会员
     *
     * @param member 会员
     * @return 更新后的会员
     */
    Member update(Member member);

    /**
     * 删除会员
     *
     * @param id 会员ID
     * @return 是否成功
     */
    boolean delete(MemberId id);

    /**
     * 更新会员状态
     *
     * @param id 会员ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(MemberId id, Integer status);

    /**
     * 更新会员等级
     *
     * @param userId 用户ID
     * @param memberLevel 会员等级
     * @return 是否成功
     */
    boolean updateMemberLevel(UserId userId, Integer memberLevel);

    /**
     * 更新积分
     *
     * @param userId 用户ID
     * @param points 积分变动
     * @return 是否成功
     */
    boolean updatePoints(UserId userId, Integer points);

    /**
     * 更新成长值
     *
     * @param userId 用户ID
     * @param growthValue 成长值变动
     * @return 是否成功
     */
    boolean updateGrowthValue(UserId userId, Integer growthValue);

    /**
     * 更新已使用积分
     *
     * @param userId 用户ID
     * @param points 积分变动
     * @return 是否成功
     */
    boolean updateUsedPoints(UserId userId, Integer points);

    /**
     * 更新免邮次数
     *
     * @param userId 用户ID
     * @param count 次数变动
     * @return 是否成功
     */
    boolean updateFreeShippingCount(UserId userId, Integer count);

    /**
     * 更新免退次数
     *
     * @param userId 用户ID
     * @param count 次数变动
     * @return 是否成功
     */
    boolean updateFreeReturnCount(UserId userId, Integer count);

    /**
     * 延长会员有效期
     *
     * @param userId 用户ID
     * @param endTime 新的结束时间
     * @return 是否成功
     */
    boolean extendMembership(UserId userId, LocalDateTime endTime);

    /**
     * 根据ID查询会员等级
     *
     * @param id 会员等级ID
     * @return 会员等级
     */
    Optional<MemberLevel> findMemberLevelById(Long id);

    /**
     * 根据等级查询会员等级
     *
     * @param level 等级
     * @return 会员等级
     */
    Optional<MemberLevel> findMemberLevelByLevel(Integer level);

    /**
     * 查询所有会员等级
     *
     * @return 会员等级列表
     */
    List<MemberLevel> findAllMemberLevels();

    /**
     * 保存会员等级
     *
     * @param memberLevel 会员等级
     * @return 保存后的会员等级
     */
    MemberLevel saveMemberLevel(MemberLevel memberLevel);

    /**
     * 更新会员等级
     *
     * @param memberLevel 会员等级
     * @return 更新后的会员等级
     */
    MemberLevel updateMemberLevel(MemberLevel memberLevel);

    /**
     * 删除会员等级
     *
     * @param id 会员等级ID
     * @return 是否成功
     */
    boolean deleteMemberLevel(Long id);

    /**
     * 根据成长值查询对应的会员等级
     *
     * @param growthValue 成长值
     * @return 会员等级
     */
    Optional<MemberLevel> findMemberLevelByGrowthValue(Integer growthValue);

    /**
     * 统计某个会员等级的会员数量
     *
     * @param memberLevel 会员等级
     * @return 会员数量
     */
    int countByMemberLevel(Integer memberLevel);

    /**
     * 根据成长值查询会员等级编号
     *
     * @param growthValue 成长值
     * @return 会员等级编号
     */
    Integer findLevelNumberByGrowthValue(Integer growthValue);

    /**
     * 检查用户是否是会员
     *
     * @param userId 用户ID
     * @return 是否是会员
     */
    boolean existsByUserId(UserId userId);

    /**
     * 获取所有会员等级
     *
     * @return 会员等级列表
     */
    List<MemberLevel> findAllLevels();

    /**
     * 通过等级获取会员等级
     *
     * @param level 等级
     * @return 会员等级对象
     */
    Optional<MemberLevel> findLevelByLevel(Integer level);

    /**
     * 通过ID获取会员等级
     *
     * @param id 会员等级ID
     * @return 会员等级对象
     */
    Optional<MemberLevel> findLevelById(Long id);

    /**
     * 通过成长值获取会员等级
     *
     * @param growthValue 成长值
     * @return 会员等级对象
     */
    Optional<MemberLevel> findLevelByGrowthValue(Integer growthValue);

    /**
     * 保存会员等级
     *
     * @param memberLevel 会员等级对象
     * @return 保存后的会员等级对象
     */
    MemberLevel saveLevel(MemberLevel memberLevel);

    /**
     * 更新会员等级
     *
     * @param memberLevel 会员等级对象
     * @return 更新后的会员等级对象
     */
    MemberLevel updateLevel(MemberLevel memberLevel);

    /**
     * 删除会员等级
     *
     * @param id 会员等级ID
     * @return 是否删除成功
     */
    boolean deleteLevel(Long id);

    /**
     * 保存用户积分记录
     *
     * @param pointsLog 用户积分记录对象
     * @return 保存后的用户积分记录对象
     */
    UserPointsLog savePointsLog(UserPointsLog pointsLog);

    /**
     * 根据用户ID分页查询积分记录
     *
     * @param userId 用户ID
     * @param pageRequest 分页参数
     * @return 积分记录分页结果
     */
    PageResult<UserPointsLog> getPointsLogsByUserId(UserId userId, PageRequest pageRequest);
} 