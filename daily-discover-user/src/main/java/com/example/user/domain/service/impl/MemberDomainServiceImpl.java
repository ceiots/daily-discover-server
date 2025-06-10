package com.example.user.domain.service.impl;

import com.example.common.exception.BusinessException;
import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.id.MemberId;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.member.Member;
import com.example.user.domain.model.member.MemberLevel;
import com.example.user.domain.repository.MemberLevelRepository;
import com.example.user.domain.repository.MemberQueryCondition;
import com.example.user.domain.repository.MemberRepository;
import com.example.user.domain.service.MemberDomainService;
import com.example.user.infrastructure.common.result.ResultCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 会员领域服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberDomainServiceImpl implements MemberDomainService {

    private final MemberRepository memberRepository;
    private final MemberLevelRepository memberLevelRepository;
    private final UserPointsLogRepository userPointsLogRepository;
    
    @Override
    public List<MemberLevel> getMemberLevels() {
        return memberLevelRepository.findAll();
    }
    
    @Override
    public Optional<MemberLevel> getMemberLevel(Integer level) {
        return memberLevelRepository.findByLevel(level);
    }
    
    @Override
    public MemberLevel createMemberLevel(MemberLevel memberLevel) {
        return memberLevelRepository.save(memberLevel);
    }
    
    @Override
    public MemberLevel updateMemberLevel(MemberLevel memberLevel) {
        return memberLevelRepository.update(memberLevel);
    }
    
    @Override
    public boolean deleteMemberLevel(Integer level) {
        return memberLevelRepository.deleteByLevel(level);
    }
    
    @Override
    public boolean existsMemberByLevel(Integer level) {
        return memberRepository.existsByLevel(level);
    }
    
    @Override
    public Optional<Member> getMemberById(MemberId memberId) {
        return memberRepository.findById(memberId);
    }
    
    @Override
    public Optional<Member> getMember(UserId userId) {
        return memberRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Member createMember(UserId userId, Integer level, Boolean isForever, Integer months) {
        // 检查用户是否已经是会员
        if (memberRepository.existsByUserId(userId)) {
            throw new BusinessException(ResultCode.MEMBER_ALREADY_EXISTS);
        }

        // 创建会员
        Member member = Member.create(userId, level, isForever, months);
        
        // 生成会员ID
        member.setId(MemberId.of(generateMemberId()));
        
        return memberRepository.save(member);
    }

    @Override
    @Transactional
    public Member upgradeMember(MemberId memberId, Integer level, Integer growthValue) {
        // 查询会员
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }

        Member member = memberOpt.get();
        
        // 增加成长值（如果有）
        if (growthValue != null && growthValue > 0) {
            member.addGrowthValue(growthValue);
        }
        
        // 升级会员
        member.upgrade(level, member.getGrowthValue());
        
        return memberRepository.update(member);
    }

    @Override
    @Transactional
    public Member extendMember(MemberId memberId, Integer months) {
        // 查询会员
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }

        Member member = memberOpt.get();
        // 延长会员时间
        member.extend(months);
        return memberRepository.update(member);
    }

    @Override
    @Transactional
    public Member setForeverMember(MemberId memberId) {
        // 查询会员
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }

        Member member = memberOpt.get();
        // 设置为永久会员
        member.setForever();
        return memberRepository.update(member);
    }

    @Override
    @Transactional
    public Member addPoints(MemberId memberId, Integer points) {
        // 查询会员
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }

        Member member = memberOpt.get();
        // 增加积分
        member.addPoints(points);
        
        // 记录积分流水（实际实现应该调用积分服务）
        // 这里简化处理，不记录积分流水
        
        return memberRepository.update(member);
    }

    @Override
    @Transactional
    public Member usePoints(MemberId memberId, Integer points) {
        // 查询会员
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }

        Member member = memberOpt.get();
        // 使用积分
        member.usePoints(points);
        memberRepository.update(member);
        
        // 记录积分流水（实际实现应该调用积分服务）
        // 这里简化处理，不记录积分流水
        
        return member;
    }

    @Override
    @Transactional
    public Member addGrowthValue(MemberId memberId, Integer growthValue) {
        // 查询会员
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }

        Member member = memberOpt.get();
        // 增加成长值
        member.addGrowthValue(growthValue);
        
        // 根据成长值查询对应的会员等级
        Optional<MemberLevel> levelOpt = memberLevelRepository.findByGrowthValue(member.getGrowthValue());
        if (levelOpt.isPresent()) {
            MemberLevel level = levelOpt.get();
            // 如果等级提升，则更新会员等级
            if (level.getLevel() > member.getMemberLevel()) {
                member.upgrade(level.getLevel(), member.getGrowthValue());
            }
        }
        
        return memberRepository.update(member);
    }

    @Transactional
    public Member addFreeShippingCount(MemberId memberId, Integer count) {
        // 查询会员
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }

        Member member = memberOpt.get();
        // 增加免邮次数
        member.addFreeShippingCount(count);
        return memberRepository.update(member);
    }

    @Transactional
    public Member useFreeShipping(MemberId memberId) {
        // 查询会员
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }

        Member member = memberOpt.get();
        // 使用免邮特权
        member.useFreeShipping();
        memberRepository.update(member);
        
        return member;
    }
    
    @Override
    public PageResult<Member> getMemberPage(PageRequest pageRequest, MemberQueryCondition condition) {
        return memberRepository.findPage(pageRequest, condition);
    }
    
    @Override
    public List<Member> getMemberList(MemberQueryCondition condition) {
        return memberRepository.findList(condition);
    }
    
    @Override
    public UserPointsLog savePointsLog(UserPointsLog pointsLog) {
        return userPointsLogRepository.save(pointsLog);
    }
    
    @Override
    public PageResult<UserPointsLog> getPointsLogsByUserId(UserId userId, PageRequest pageRequest) {
        return userPointsLogRepository.findByUserId(userId, pageRequest);
    }

    @Override
    @Transactional
    public Member addFreeReturnCount(MemberId memberId, int count) {
        // 查询会员
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }

        Member member = memberOpt.get();
        // 增加免退次数
        member.addFreeReturnCount(count);
        return memberRepository.update(member);
    }

    @Override
    @Transactional
    public boolean useFreeReturn(MemberId memberId) {
        // 查询会员
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }

        Member member = memberOpt.get();
        // 使用免退特权
        boolean result = member.useFreeReturn();
        if (result) {
            memberRepository.update(member);
        }
        
        return result;
    }

    @Override
    @Transactional
    public boolean disableMember(MemberId memberId) {
        // 查询会员
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }

        Member member = memberOpt.get();
        // 禁用会员
        member.disable();
        memberRepository.update(member);
        return true;
    }

    @Override
    @Transactional
    public boolean enableMember(MemberId memberId) {
        // 查询会员
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }

        Member member = memberOpt.get();
        // 启用会员
        member.enable();
        memberRepository.update(member);
        return true;
    }
    
    /**
     * 生成会员ID
     * 实际实现应该使用分布式ID生成器
     * 这里简化处理，使用当前时间戳
     */
    private Long generateMemberId() {
        return System.currentTimeMillis();
    }
} 