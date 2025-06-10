package com.example.user.application.service.impl;

import com.example.common.exception.BusinessException;
import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.application.assembler.MemberAssembler;
import com.example.user.application.dto.MemberDTO;
import com.example.user.application.dto.MemberLevelDTO;
import com.example.user.application.dto.PointsLogDTO;
import com.example.user.application.service.MemberService;
import com.example.user.domain.model.UserPointsLog;
import com.example.user.domain.model.id.MemberId;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.member.Member;
import com.example.user.domain.model.member.MemberLevel;
import com.example.user.domain.repository.MemberQueryCondition;
import com.example.user.domain.service.BaseDomainService;
import com.example.user.domain.service.MemberDomainService;
import com.example.user.infrastructure.common.result.ResultCode;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 会员应用服务实现类
 */
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberDomainService memberDomainService;
    private final MemberAssembler memberAssembler;

    @Override
    public BaseDomainService getBaseDomainService() {
        return memberDomainService; // MemberDomainService继承了BaseDomainService
    }

    @Override
    public MemberDTO getMemberInfo(Long userId) {
        return getMemberByUserId(userId);
    }
    
    @Override
    public MemberDTO getMemberByUserId(Long userId) {
        Optional<Member> memberOpt = memberDomainService.getMember(new UserId(userId));
        
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        return memberAssembler.toDTO(memberOpt.get());
    }

    @Override
    public MemberDTO getMemberById(Long memberId) {
        Optional<Member> memberOpt = memberDomainService.getMemberById(new MemberId(memberId));
        
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        return memberAssembler.toDTO(memberOpt.get());
    }

    @Override
    @Transactional
    public MemberDTO openMember(Long userId, Integer level, Boolean isForever, Integer months) {
        return createMember(userId, level, isForever, months);
    }
    
    @Override
    @Transactional
    public MemberDTO createMember(Long userId, Integer level, Boolean isForever, Integer months) {
        // 检查用户是否已经是会员
        Optional<Member> existingMember = memberDomainService.getMember(new UserId(userId));
        if (existingMember.isPresent()) {
            throw new BusinessException(ResultCode.MEMBER_ALREADY_EXISTS);
        }
        
        // 检查会员等级是否存在
        Optional<MemberLevel> memberLevelOpt = memberDomainService.getMemberLevel(level);
        if (memberLevelOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_LEVEL_NOT_FOUND);
        }
        
        // 创建会员
        Member member = memberDomainService.createMember(new UserId(userId), level, isForever, months);
        
        return memberAssembler.toDTO(member);
    }

    
    @Override
    @Transactional
    public MemberDTO upgradeMember(Long memberId, Integer level, Integer growthValue) {
        // 检查会员是否存在
        Optional<Member> memberOpt = memberDomainService.getMemberById(new MemberId(memberId));
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        // 检查会员等级是否存在
        Optional<MemberLevel> memberLevelOpt = memberDomainService.getMemberLevel(level);
        if (memberLevelOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_LEVEL_NOT_FOUND);
        }
        
        // 检查是否是升级
        Member member = memberOpt.get();
        if (member.getMemberLevel() >= level) {
            throw new BusinessException(ResultCode.MEMBER_LEVEL_INVALID);
        }
        
        // 升级会员
        Member upgradedMember = memberDomainService.upgradeMember(new MemberId(memberId), level, growthValue);
        
        return memberAssembler.toDTO(upgradedMember);
    }

    @Override
    @Transactional
    public MemberDTO renewMember(Long userId, Integer months) {
        // 查找用户会员信息
        Optional<Member> memberOpt = memberDomainService.getMember(new UserId(userId));
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        // 延长会员有效期
        Member member = memberOpt.get();
        Member extendedMember = memberDomainService.extendMember(member.getId(), months);
        
        return memberAssembler.toDTO(extendedMember);
    }
    
    @Override
    @Transactional
    public MemberDTO extendMember(Long memberId, Integer months) {
        // 检查会员是否存在
        Optional<Member> memberOpt = memberDomainService.getMemberById(new MemberId(memberId));
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        // 延长会员有效期
        Member extendedMember = memberDomainService.extendMember(new MemberId(memberId), months);
        
        return memberAssembler.toDTO(extendedMember);
    }

    @Override
    @Transactional
    public MemberDTO setForeverMember(Long memberId) {
        // 检查会员是否存在
        Optional<Member> memberOpt = memberDomainService.getMemberById(new MemberId(memberId));
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        // 设置为永久会员
        Member foreverMember = memberDomainService.setForeverMember(new MemberId(memberId));
        
        return memberAssembler.toDTO(foreverMember);
    }

    @Override
    @Transactional
    public MemberDTO disableMember(Long memberId) {
        // 检查会员是否存在
        Optional<Member> memberOpt = memberDomainService.getMemberById(new MemberId(memberId));
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        // 禁用会员
        Member disabledMember = memberDomainService.disableMember(new MemberId(memberId));
        
        return memberAssembler.toDTO(disabledMember);
    }

    @Override
    @Transactional
    public MemberDTO enableMember(Long memberId) {
        // 检查会员是否存在
        Optional<Member> memberOpt = memberDomainService.getMemberById(new MemberId(memberId));
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        // 启用会员
        Member enabledMember = memberDomainService.enableMember(new MemberId(memberId));
        
        return memberAssembler.toDTO(enabledMember);
    }

    @Override
    @Transactional
    public MemberDTO addGrowthValue(Long memberId, Integer growthValue, String description) {
        // 检查会员是否存在
        Optional<Member> memberOpt = memberDomainService.getMemberById(new MemberId(memberId));
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        // 增加成长值
        Member member = memberDomainService.addGrowthValue(new MemberId(memberId), growthValue);
        
        return memberAssembler.toDTO(member);
    }

    @Override
    @Transactional
    public MemberDTO addPoints(Long memberId, Integer points, String description) {
        // 检查会员是否存在
        Optional<Member> memberOpt = memberDomainService.getMemberById(new MemberId(memberId));
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        // 增加积分
        Member member = memberDomainService.addPoints(new MemberId(memberId), points);
        
        // 记录积分日志
        Member updatedMember = memberOpt.get();
        UserPointsLog pointsLog = UserPointsLog.createGainLog(
            updatedMember.getUserId(),
            points,
            updatedMember.getPoints() - points,
            updatedMember.getPoints(),
            5, // 系统
            null,
            description
        );
        memberDomainService.savePointsLog(pointsLog);
        
        return memberAssembler.toDTO(member);
    }

    @Override
    @Transactional
    public MemberDTO usePoints(Long memberId, Integer points, String description) {
        // 检查会员是否存在
        Optional<Member> memberOpt = memberDomainService.getMemberById(new MemberId(memberId));
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        Member member = memberOpt.get();
        
        // 检查积分是否足够
        if (!memberDomainService.canUsePoints(member, points)) {
            throw new BusinessException(ResultCode.POINTS_NOT_ENOUGH);
        }
        
        // 使用积分
        Member updatedMember = memberDomainService.usePoints(new MemberId(memberId), points);
        
        // 记录积分日志
        UserPointsLog pointsLog = UserPointsLog.createUseLog(
            updatedMember.getUserId(),
            points,
            member.getPoints(),
            updatedMember.getPoints(),
            5, // 系统
            null,
            description
        );
        memberDomainService.savePointsLog(pointsLog);
        
        return memberAssembler.toDTO(updatedMember);
    }

    @Override
    public List<MemberLevelDTO> getMemberLevels() {
        List<MemberLevel> memberLevels = memberDomainService.getMemberLevels();
        return memberAssembler.toLevelDTOList(memberLevels);
    }

    @Override
    public MemberLevelDTO getMemberLevel(Integer level) {
        Optional<MemberLevel> memberLevelOpt = memberDomainService.getMemberLevel(level);
        
        if (memberLevelOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_LEVEL_NOT_FOUND);
        }
        
        return memberAssembler.toLevelDTO(memberLevelOpt.get());
    }
    
    @Override
    public MemberLevelDTO getMemberLevelById(Long id) {
        Optional<MemberLevel> memberLevelOpt = memberDomainService.getMemberLevelById(id);
        
        if (memberLevelOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_LEVEL_NOT_FOUND);
        }
        
        return memberAssembler.toLevelDTO(memberLevelOpt.get());
    }

    @Override
    @Transactional
    public MemberLevelDTO createMemberLevel(MemberLevelDTO memberLevelDTO) {
        // 检查等级是否已存在
        Optional<MemberLevel> existingLevelOpt = memberDomainService.getMemberLevel(memberLevelDTO.getLevel());
        if (existingLevelOpt.isPresent()) {
            throw new BusinessException(ResultCode.MEMBER_LEVEL_EXISTS);
        }
        
        // 创建会员等级
        MemberLevel memberLevel = memberAssembler.toLevelEntity(memberLevelDTO);
        MemberLevel createdMemberLevel = memberDomainService.createMemberLevel(memberLevel);
        
        return memberAssembler.toLevelDTO(createdMemberLevel);
    }

    @Override
    @Transactional
    public MemberLevelDTO updateMemberLevel(MemberLevelDTO memberLevelDTO) {
        // 检查等级是否存在
        Optional<MemberLevel> existingLevelOpt = memberDomainService.getMemberLevel(memberLevelDTO.getLevel());
        if (existingLevelOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_LEVEL_NOT_FOUND);
        }
        
        // 更新会员等级
        MemberLevel memberLevel = memberAssembler.toLevelEntity(memberLevelDTO);
        MemberLevel updatedMemberLevel = memberDomainService.updateMemberLevel(memberLevel);
        
        return memberAssembler.toLevelDTO(updatedMemberLevel);
    }

    @Override
    @Transactional
    public boolean deleteMemberLevel(Integer level) {
        // 检查等级是否存在
        Optional<MemberLevel> existingLevelOpt = memberDomainService.getMemberLevel(level);
        if (existingLevelOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_LEVEL_NOT_FOUND);
        }
        
        // 检查是否有会员使用该等级
        if (memberDomainService.existsMemberByLevel(level)) {
            throw new BusinessException(ResultCode.MEMBER_LEVEL_IN_USE);
        }
        
        // 删除会员等级
        return memberDomainService.deleteMemberLevel(level);
    }

    @Override
    public PageResult<PointsLogDTO> getPointsLogs(Long memberId, PageRequest pageRequest) {
        // 检查会员是否存在
        Optional<Member> memberOpt = memberDomainService.getMemberById(new MemberId(memberId));
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        // 获取积分记录
        UserId userId = memberOpt.get().getUserId();
        PageResult<UserPointsLog> pageResult = memberDomainService.getPointsLogsByUserId(userId, pageRequest);
        
        List<PointsLogDTO> pointsLogDTOs = memberAssembler.toPointsLogDTOList(pageResult.getList());
        
        return new PageResult<>(pointsLogDTOs, pageResult.getTotal(), pageResult.getPages(), pageResult.getPageNum(), pageResult.getPageSize());
    }

    @Override
    public PageResult<MemberDTO> getMemberPage(PageRequest pageRequest, MemberQueryCondition condition) {
        PageResult<Member> pageResult = memberDomainService.getMemberPage(pageRequest, condition);
        
        List<MemberDTO> memberDTOs = pageResult.getList().stream()
                .map(memberAssembler::toDTO)
                .collect(Collectors.toList());
        
        return new PageResult<>(memberDTOs, pageResult.getTotal(), pageResult.getPages(), pageResult.getPageNum(), pageResult.getPageSize());
    }

    @Override
    public List<MemberDTO> getMemberList(MemberQueryCondition condition) {
        List<Member> memberList = memberDomainService.getMemberList(condition);
        
        return memberList.stream()
                .map(memberAssembler::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValidMember(Long memberId) {
        Optional<Member> memberOpt = memberDomainService.getMemberById(new MemberId(memberId));
        return memberOpt.isPresent() && memberDomainService.isValidMember(memberOpt.get());
    }

    @Override
    public int calculateGrowthToNextLevel(Long memberId) {
        // 检查会员是否存在
        Optional<Member> memberOpt = memberDomainService.getMemberById(new MemberId(memberId));
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        Member member = memberOpt.get();
        return memberDomainService.calculateGrowthToNextLevel(member.getMemberLevel(), member.getGrowthValue());
    }
    
    @Override
    @Transactional
    public boolean cancelMember(Long userId) {
        // 查找用户会员信息
        Optional<Member> memberOpt = memberDomainService.getMember(new UserId(userId));
        if (memberOpt.isEmpty()) {
            return false; // 用户不是会员，无需取消
        }
        
        // 禁用会员
        Member member = memberOpt.get();
        memberDomainService.disableMember(member.getId());
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean freezeMember(Long userId) {
        // 查找用户会员信息
        Optional<Member> memberOpt = memberDomainService.getMember(new UserId(userId));
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        // 禁用会员
        Member member = memberOpt.get();
        memberDomainService.disableMember(member.getId());
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean unfreezeMember(Long userId) {
        // 查找用户会员信息
        Optional<Member> memberOpt = memberDomainService.getMember(new UserId(userId));
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        // 启用会员
        Member member = memberOpt.get();
        memberDomainService.enableMember(member.getId());
        
        return true;
    }
    
    @Override
    @Transactional
    public MemberDTO addGrowth(Long userId, Integer growthValue, Integer source, String sourceId) {
        // 查找用户会员信息
        Optional<Member> memberOpt = memberDomainService.getMember(new UserId(userId));
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        // 增加成长值
        Member member = memberOpt.get();
        String description = "来源:" + source + (sourceId != null ? ",ID:" + sourceId : "");
        Member updatedMember = memberDomainService.addGrowthValue(member.getId(), growthValue);
        
        return memberAssembler.toDTO(updatedMember);
    }
    
    @Override
    @Transactional
    public MemberDTO addPoints(Long userId, Integer points, Integer source, String sourceId) {
        // 查找用户会员信息
        Optional<Member> memberOpt = memberDomainService.getMember(new UserId(userId));
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        // 增加积分
        Member member = memberOpt.get();
        String description = "来源:" + source + (sourceId != null ? ",ID:" + sourceId : "");
        Member updatedMember = memberDomainService.addPoints(member.getId(), points);
        
        // 记录积分日志
        UserPointsLog pointsLog = UserPointsLog.createGainLog(
            updatedMember.getUserId(),
            points,
            member.getPoints(),
            updatedMember.getPoints(),
            source,
            sourceId,
            description
        );
        memberDomainService.savePointsLog(pointsLog);
        
        return memberAssembler.toDTO(updatedMember);
    }
    
    @Override
    @Transactional
    public MemberDTO usePoints(Long userId, Integer points, Integer source, String sourceId) {
        // 查找用户会员信息
        Optional<Member> memberOpt = memberDomainService.getMember(new UserId(userId));
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        Member member = memberOpt.get();
        
        // 检查积分是否足够
        if (!memberDomainService.canUsePoints(member, points)) {
            throw new BusinessException(ResultCode.POINTS_NOT_ENOUGH);
        }
        
        // 使用积分
        String description = "来源:" + source + (sourceId != null ? ",ID:" + sourceId : "");
        Member updatedMember = memberDomainService.usePoints(member.getId(), points);
        
        // 记录积分日志
        UserPointsLog pointsLog = UserPointsLog.createUseLog(
            updatedMember.getUserId(),
            points,
            member.getPoints(),
            updatedMember.getPoints(),
            source,
            sourceId,
            description
        );
        memberDomainService.savePointsLog(pointsLog);
        
        return memberAssembler.toDTO(updatedMember);
    }
    
    
   
    
    @Override
    @Transactional
    public MemberDTO upgradeMember(Long userId, Integer level) {
        // 查找用户会员信息
        Optional<Member> memberOpt = memberDomainService.getMember(new UserId(userId));
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        // 检查会员等级是否存在
        Optional<MemberLevel> memberLevelOpt = memberDomainService.getMemberLevel(level);
        if (memberLevelOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_LEVEL_NOT_FOUND);
        }
        
        // 检查是否是升级
        Member member = memberOpt.get();
        if (member.getMemberLevel() >= level) {
            throw new BusinessException(ResultCode.MEMBER_LEVEL_INVALID);
        }
        
        // 计算所需成长值
        MemberLevel targetLevel = memberLevelOpt.get();
        int requiredGrowthValue = targetLevel.getGrowthMin() - member.getGrowthValue();
        if (requiredGrowthValue > 0) {
            // 自动增加成长值到目标等级
            member = memberDomainService.addGrowthValue(member.getId(), requiredGrowthValue);
        }
        
        // 升级会员
        Member upgradedMember = memberDomainService.upgradeMember(member.getId(), level, 0);
        
        return memberAssembler.toDTO(upgradedMember);
    }
    
}
