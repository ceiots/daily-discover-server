package com.example.user.domain.repository;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.id.MemberId;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.member.Member;
import com.example.user.domain.model.MemberLevel;

import java.util.List;
import java.util.Optional;

/**
 * 会员仓储接口
 */
public interface MemberRepository {
    /**
     * 根据ID查询会员
     *
     * @param memberId 会员ID
     * @return 会员
     */
    Optional<Member> findById(MemberId memberId);

    /**
     * 根据用户ID查询会员
     *
     * @param userId 用户ID
     * @return 会员
     */
    Optional<Member> findByUserId(UserId userId);

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
     * @param memberId 会员ID
     * @return 是否删除成功
     */
    boolean delete(MemberId memberId);

    /**
     * 更新会员状态
     *
     * @param memberId 会员ID
     * @param status 状态
     * @return 是否更新成功
     */
    boolean updateStatus(MemberId memberId, Integer status);

    /**
     * 分页查询会员
     *
     * @param pageRequest 分页请求参数
     * @param condition 查询条件
     * @return 会员分页结果
     */
    PageResult<Member> findPage(PageRequest pageRequest, MemberQueryCondition condition);

    /**
     * 查询会员列表
     *
     * @param condition 查询条件
     * @return 会员列表
     */
    List<Member> findList(MemberQueryCondition condition);

    /**
     * 根据会员等级查询会员列表
     *
     * @param level 会员等级
     * @return 会员列表
     */
    List<Member> findByLevel(Integer level);

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
} 