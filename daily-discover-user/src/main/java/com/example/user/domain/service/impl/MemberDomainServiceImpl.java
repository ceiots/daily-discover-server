package com.example.user.domain.service.impl;

import com.example.common.exception.BusinessException;
import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.UserPointsLog;
import com.example.user.domain.model.id.MemberId;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.member.Member;
import com.example.user.domain.model.member.MemberLevel;
import com.example.user.domain.model.valueobject.Email;
import com.example.user.domain.model.valueobject.Mobile;
import com.example.user.domain.model.valueobject.Password;
import com.example.user.domain.repository.MemberLevelRepository;
import com.example.user.domain.repository.MemberQueryCondition;
import com.example.user.domain.repository.MemberRepository;
import com.example.user.domain.repository.UserPointsLogRepository;
import com.example.user.domain.service.MemberDomainService;
import com.example.user.infrastructure.common.result.ResultCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

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
    
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,16}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$");
    
    // BaseDomainService 接口实现
    @Override
    public boolean verifyPassword(String plainPassword, String encodedPassword, String salt) {
        return PASSWORD_ENCODER.matches(plainPassword, encodedPassword);
    }

    @Override
    public String encryptPassword(String plainPassword, String salt) {
        return PASSWORD_ENCODER.encode(plainPassword);
    }

    @Override
    public String generateSalt() {
        return String.valueOf(System.currentTimeMillis());
    }

    @Override
    public boolean isValidMobile(String mobile) {
        return mobile != null && MOBILE_PATTERN.matcher(mobile).matches();
    }

    @Override
    public boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }

    @Override
    public String generateVerifyCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    @Override
    public boolean validatePasswordStrength(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }
    
    // 会员领域服务方法实现
    @Override
    public List<MemberLevel> getMemberLevels() {
        return memberLevelRepository.findAll();
    }
    
    @Override
    public Optional<MemberLevel> getMemberLevel(Integer level) {
        return memberLevelRepository.findByLevel(level);
    }
    
    @Override
    public Optional<MemberLevel> getMemberLevelById(Long id) {
        return memberLevelRepository.findById(id);
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
        Optional<MemberLevel> levelOpt = memberLevelRepository.findByLevel(level);
        if (levelOpt.isPresent()) {
            return memberLevelRepository.delete(levelOpt.get().getId());
        }
        return false;
    }
    
    @Override
    public boolean existsMemberByLevel(Integer level) {
        List<Member> members = memberRepository.findByMemberLevel(level);
        return members != null && !members.isEmpty();
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
        member.setId(new MemberId(generateMemberId()));
        
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
        // 简化实现，实际应该根据条件查询
        Integer status = condition.getStatus();
        Integer level = condition.getLevel();
        return memberRepository.findPage(pageRequest, status, level);
    }
    
    @Override
    public List<Member> getMemberList(MemberQueryCondition condition) {
        // 简化实现，实际应该根据条件查询
        if (condition.getLevel() != null) {
            return memberRepository.findByMemberLevel(condition.getLevel());
        } else if (condition.getStatus() != null) {
            return memberRepository.findByStatus(condition.getStatus());
        } else {
            // 返回空列表，实际实现应该提供更多查询方式
            return List.of();
        }
    }
    
    @Override
    public UserPointsLog savePointsLog(UserPointsLog pointsLog) {
        return userPointsLogRepository.save(pointsLog);
    }
    
    @Override
    public PageResult<UserPointsLog> getPointsLogsByUserId(UserId userId, PageRequest pageRequest) {
        // 直接调用仓储接口的方法
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
    public Member disableMember(MemberId memberId) {
        // 查询会员
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }

        Member member = memberOpt.get();
        // 禁用会员
        member.disable();
        return memberRepository.update(member);
    }

    @Override
    @Transactional
    public Member enableMember(MemberId memberId) {
        // 查询会员
        Optional<Member> memberOpt = memberRepository.findById(memberId);
        if (memberOpt.isEmpty()) {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }

        Member member = memberOpt.get();
        // 启用会员
        member.enable();
        return memberRepository.update(member);
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