package com.example.user.application.service;

import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.application.dto.MemberDTO;
import com.example.user.application.dto.MemberLevelDTO;
import com.example.user.application.dto.PointsLogDTO;
import com.example.user.domain.repository.MemberQueryCondition;
import com.example.user.domain.service.BaseDomainService;

import java.util.List;

/**
 * 会员应用服务接口
 */
public interface MemberService extends BaseApplicationService {

    /**
     * 获取会员信息
     *
     * @param userId 用户ID
     * @return 会员DTO
     */
    MemberDTO getMemberByUserId(Long userId);

    /**
     * 获取会员信息
     *
     * @param memberId 会员ID
     * @return 会员DTO
     */
    MemberDTO getMemberById(Long memberId);

    /**
     * 创建会员
     *
     * @param userId    用户ID
     * @param level     会员等级
     * @param isForever 是否永久会员
     * @param months    会员有效期（月）
     * @return 会员DTO
     */
    MemberDTO createMember(Long userId, Integer level, Boolean isForever, Integer months);

    /**
     * 升级会员
     *
     * @param memberId    会员ID
     * @param level       会员等级
     * @param growthValue 成长值
     * @return 会员DTO
     */
    MemberDTO upgradeMember(Long memberId, Integer level, Integer growthValue);

    /**
     * 延长会员有效期
     *
     * @param memberId 会员ID
     * @param months   延长月数
     * @return 会员DTO
     */
    MemberDTO extendMember(Long memberId, Integer months);

    /**
     * 设置为永久会员
     *
     * @param memberId 会员ID
     * @return 会员DTO
     */
    MemberDTO setForeverMember(Long memberId);

    /**
     * 禁用会员
     *
     * @param memberId 会员ID
     * @return 会员DTO
     */
    MemberDTO disableMember(Long memberId);

    /**
     * 启用会员
     *
     * @param memberId 会员ID
     * @return 会员DTO
     */
    MemberDTO enableMember(Long memberId);

    /**
     * 增加成长值
     *
     * @param memberId    会员ID
     * @param growthValue 成长值
     * @param description 描述
     * @return 会员DTO
     */
    MemberDTO addGrowthValue(Long memberId, Integer growthValue, String description);

    /**
     * 增加积分
     *
     * @param memberId    会员ID
     * @param points      积分
     * @param description 描述
     * @return 会员DTO
     */
    MemberDTO addPoints(Long memberId, Integer points, String description);

    /**
     * 使用积分
     *
     * @param memberId    会员ID
     * @param points      积分
     * @param description 描述
     * @return 会员DTO
     */
    MemberDTO usePoints(Long memberId, Integer points, String description);

    /**
     * 获取会员等级列表
     *
     * @return 会员等级列表
     */
    List<MemberLevelDTO> getMemberLevels();

    /**
     * 获取会员等级
     *
     * @param level 等级
     * @return 会员等级DTO
     */
    MemberLevelDTO getMemberLevel(Integer level);

    /**
     * 创建会员等级
     *
     * @param memberLevelDTO 会员等级DTO
     * @return 创建后的会员等级DTO
     */
    MemberLevelDTO createMemberLevel(MemberLevelDTO memberLevelDTO);

    /**
     * 更新会员等级
     *
     * @param memberLevelDTO 会员等级DTO
     * @return 更新后的会员等级DTO
     */
    MemberLevelDTO updateMemberLevel(MemberLevelDTO memberLevelDTO);

    /**
     * 删除会员等级
     *
     * @param level 等级
     * @return 是否删除成功
     */
    boolean deleteMemberLevel(Integer level);

    /**
     * 获取积分记录列表
     *
     * @param memberId    会员ID
     * @param pageRequest 分页参数
     * @return 积分记录分页结果
     */
    PageResult<PointsLogDTO> getPointsLogs(Long memberId, PageRequest pageRequest);

    /**
     * 分页查询会员
     *
     * @param pageRequest 分页参数
     * @param condition   查询条件
     * @return 会员分页结果
     */
    PageResult<MemberDTO> getMemberPage(PageRequest pageRequest, MemberQueryCondition condition);

    /**
     * 查询会员列表
     *
     * @param condition 查询条件
     * @return 会员列表
     */
    List<MemberDTO> getMemberList(MemberQueryCondition condition);

    /**
     * 检查会员是否有效
     *
     * @param memberId 会员ID
     * @return 是否有效
     */
    boolean isValidMember(Long memberId);

    /**
     * 计算会员下一等级所需成长值
     *
     * @param memberId 会员ID
     * @return 升级所需成长值
     */
    int calculateGrowthToNextLevel(Long memberId);

    /**
     * 获取基础领域服务
     *
     * @return 基础领域服务
     */
    @Override
    BaseDomainService getBaseDomainService();
} 