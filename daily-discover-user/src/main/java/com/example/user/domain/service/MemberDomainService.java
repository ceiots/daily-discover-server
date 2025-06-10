package com.example.user.domain.service;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.UserPointsLog;
import com.example.user.domain.model.id.MemberId;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.member.Member;
import com.example.user.domain.model.member.MemberLevel;
import com.example.user.domain.repository.MemberQueryCondition;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 会员领域服务接口
 */
public interface MemberDomainService extends BaseDomainService {

    /**
     * 创建会员
     *
     * @param userId 用户ID
     * @param level  会员等级
     * @param isForever 是否永久会员
     * @param months 会员有效期（月）
     * @return 会员对象
     */
    Member createMember(UserId userId, Integer level, Boolean isForever, Integer months);

    /**
     * 获取会员信息
     *
     * @param userId 用户ID
     * @return 会员对象
     */
    Optional<Member> getMember(UserId userId);

    /**
     * 获取会员信息
     *
     * @param memberId 会员ID
     * @return 会员对象
     */
    Optional<Member> getMemberById(MemberId memberId);

    /**
     * 升级会员
     *
     * @param memberId 会员ID
     * @param level    会员等级
     * @param growthValue 成长值
     * @return 升级后的会员对象
     */
    Member upgradeMember(MemberId memberId, Integer level, Integer growthValue);

    /**
     * 延长会员有效期
     *
     * @param memberId 会员ID
     * @param months   延长月数
     * @return 延长后的会员对象
     */
    Member extendMember(MemberId memberId, Integer months);

    /**
     * 设置为永久会员
     *
     * @param memberId 会员ID
     * @return 设置后的会员对象
     */
    Member setForeverMember(MemberId memberId);

    /**
     * 禁用会员
     *
     * @param memberId 会员ID
     * @return 禁用后的会员对象
     */
    Member disableMember(MemberId memberId);

    /**
     * 启用会员
     *
     * @param memberId 会员ID
     * @return 启用后的会员对象
     */
    Member enableMember(MemberId memberId);

    /**
     * 增加成长值
     *
     * @param memberId    会员ID
     * @param growthValue 成长值
     * @return 增加后的会员对象
     */
    Member addGrowthValue(MemberId memberId, Integer growthValue);

    /**
     * 增加积分
     *
     * @param memberId 会员ID
     * @param points   积分
     * @return 增加后的会员对象
     */
    Member addPoints(MemberId memberId, Integer points);

    /**
     * 使用积分
     *
     * @param memberId 会员ID
     * @param points   积分
     * @return 使用后的会员对象
     */
    Member usePoints(MemberId memberId, Integer points);

    /**
     * 获取会员等级列表
     *
     * @return 会员等级列表
     */
    List<MemberLevel> getMemberLevels();

    /**
     * 获取会员等级
     *
     * @param level 等级
     * @return 会员等级对象
     */
    Optional<MemberLevel> getMemberLevel(Integer level);

    /**
     * 获取会员等级
     *
     * @param id 会员等级ID
     * @return 会员等级对象
     */
    Optional<MemberLevel> getMemberLevelById(Long id);

    /**
     * 创建会员等级
     *
     * @param memberLevel 会员等级对象
     * @return 创建后的会员等级对象
     */
    MemberLevel createMemberLevel(MemberLevel memberLevel);

    /**
     * 更新会员等级
     *
     * @param memberLevel 会员等级对象
     * @return 更新后的会员等级对象
     */
    MemberLevel updateMemberLevel(MemberLevel memberLevel);

    /**
     * 删除会员等级
     *
     * @param level 等级
     * @return 是否删除成功
     */
    boolean deleteMemberLevel(Integer level);
    
    /**
     * 检查会员是否有效
     * 
     * @param member 会员对象
     * @return 是否有效
     */
    default boolean isValidMember(Member member) {
        if (member == null) {
            return false;
        }
        
        // 检查会员状态
        if (member.getStatus() != 1) {
            return false;
        }
        
        // 检查会员有效期
        if (member.getIsForever()) {
            return true;
        }
        
        LocalDateTime endTime = member.getEndTime();
        return endTime != null && endTime.isAfter(LocalDateTime.now());
    }
    
    /**
     * 计算会员下一等级所需成长值
     * 
     * @param currentLevel 当前等级
     * @param currentGrowth 当前成长值
     * @return 升级所需成长值，如果已是最高等级则返回0
     */
    default int calculateGrowthToNextLevel(Integer currentLevel, Integer currentGrowth) {
        Optional<MemberLevel> currentLevelOpt = getMemberLevel(currentLevel);
        Optional<MemberLevel> nextLevelOpt = getMemberLevel(currentLevel + 1);
        
        if (currentLevelOpt.isEmpty() || nextLevelOpt.isEmpty()) {
            return 0; // 当前等级不存在或已是最高等级
        }
        
        int nextLevelMinGrowth = nextLevelOpt.get().getGrowthMin();
        return Math.max(0, nextLevelMinGrowth - currentGrowth);
    }
    
    /**
     * 根据成长值计算会员等级
     * 
     * @param growthValue 成长值
     * @return 对应的会员等级
     */
    default int calculateLevelByGrowth(int growthValue) {
        List<MemberLevel> levels = getMemberLevels();
        
        // 按等级降序排序
        levels.sort((a, b) -> Integer.compare(b.getLevel(), a.getLevel()));
        
        // 找到第一个成长值下限小于等于当前成长值的等级
        for (MemberLevel level : levels) {
            if (growthValue >= level.getGrowthMin()) {
                return level.getLevel();
            }
        }
        
        // 默认返回1级
        return 1;
    }
    
    /**
     * 检查是否可以使用积分
     * 
     * @param member 会员对象
     * @param points 要使用的积分
     * @return 是否可以使用
     */
    default boolean canUsePoints(Member member, Integer points) {
        return member != null && member.getPoints() >= points;
    }

    /**
     * 检查会员等级是否存在会员使用
     *
     * @param level 会员等级
     * @return 是否存在会员使用
     */
    boolean existsMemberByLevel(Integer level);
    
    /**
     * 保存积分日志
     *
     * @param pointsLog 积分日志
     * @return 保存后的积分日志
     */
    UserPointsLog savePointsLog(UserPointsLog pointsLog);
    
    /**
     * 获取用户积分日志分页
     *
     * @param userId 用户ID
     * @param pageRequest 分页请求
     * @return 积分日志分页
     */
    PageResult<UserPointsLog> getPointsLogsByUserId(UserId userId, PageRequest pageRequest);
    
    /**
     * 分页查询会员
     *
     * @param pageRequest 分页请求
     * @param condition 查询条件
     * @return 会员分页
     */
    PageResult<Member> getMemberPage(PageRequest pageRequest, MemberQueryCondition condition);
    
    /**
     * 查询会员列表
     *
     * @param condition 查询条件
     * @return 会员列表
     */
    List<Member> getMemberList(MemberQueryCondition condition);

    /**
     * 增加免退次数
     *
     * @param memberId 会员ID
     * @param count 次数
     * @return 会员对象
     */
    Member addFreeReturnCount(MemberId memberId, int count);
    
    /**
     * 使用免退特权
     *
     * @param memberId 会员ID
     * @return 是否使用成功
     */
    boolean useFreeReturn(MemberId memberId);
} 