package com.example.user.application.service.impl;

import com.example.common.exception.BusinessException;
import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.application.assembler.UserPointsLogAssembler;
import com.example.user.application.dto.UserPointsLogDTO;
import com.example.user.application.service.UserPointsLogService;
import com.example.user.domain.model.UserPointsLog;
import com.example.user.domain.model.id.MemberId;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.member.Member;
import com.example.user.domain.repository.UserPointsLogRepository;
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
 * 用户积分记录应用服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserPointsLogServiceImpl implements UserPointsLogService {

    private final UserPointsLogRepository userPointsLogRepository;
    private final MemberDomainService memberDomainService;
    private final UserPointsLogAssembler userPointsLogAssembler;

    @Override
    public BaseDomainService getBaseDomainService() {
        return memberDomainService;
    }

    @Override
    public PageResult<UserPointsLogDTO> getPointsLogs(Long userId, PageRequest pageRequest) {
        PageResult<UserPointsLog> pageResult = userPointsLogRepository.findByUserId(new UserId(userId), pageRequest);
        
        List<UserPointsLogDTO> userPointsLogDTOList = pageResult.getList().stream()
                .map(userPointsLogAssembler::toDTO)
                .collect(Collectors.toList());
        
        return new PageResult<>(pageResult.getPageNum(), pageResult.getPageSize(), 
                pageResult.getTotal(), userPointsLogDTOList);
    }

    @Override
    public List<UserPointsLogDTO> getPointsLogs(Long userId, Integer type, Integer limit) {
        // 由于仓储接口没有直接提供按类型查询的方法，我们需要先获取所有记录，然后在应用层过滤
        List<UserPointsLog> allLogs = userPointsLogRepository.findByUserId(new UserId(userId));
        
        // 根据类型过滤
        List<UserPointsLog> userPointsLogList = allLogs.stream()
                .filter(log -> type == null || log.getType().equals(type))
                .limit(limit != null ? limit : Long.MAX_VALUE)
                .collect(Collectors.toList());
        
        return userPointsLogList.stream()
                .map(userPointsLogAssembler::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserPointsLogDTO addPoints(Long userId, Integer points, Integer source, String sourceId, String description) {
        // 获取用户当前积分
        Integer currentPoints = getTotalPoints(userId);
        
        // 创建积分记录
        UserPointsLog userPointsLog = UserPointsLog.createGainLog(
                new UserId(userId),
                points,
                currentPoints,
                currentPoints + points,
                source,
                sourceId,
                description
        );
        
        // 保存积分记录
        UserPointsLog savedUserPointsLog = userPointsLogRepository.save(userPointsLog);
        
        // 更新用户积分
        Optional<Member> memberOpt = memberDomainService.getMember(new UserId(userId));
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            memberDomainService.addPoints(member.getId(), points);
        } else {
            // 如果用户不是会员，创建会员
            Member newMember = memberDomainService.createMember(new UserId(userId), 1, false, null);
            memberDomainService.addPoints(newMember.getId(), points);
        }
        
        return userPointsLogAssembler.toDTO(savedUserPointsLog);
    }

    @Override
    @Transactional
    public UserPointsLogDTO usePoints(Long userId, Integer points, Integer source, String sourceId, String description) {
        // 获取用户当前积分
        Integer currentPoints = getTotalPoints(userId);
        
        // 检查积分是否足够
        if (currentPoints < points) {
            throw new BusinessException(ResultCode.POINTS_NOT_ENOUGH);
        }
        
        // 创建积分记录
        UserPointsLog userPointsLog = UserPointsLog.createUseLog(
                new UserId(userId),
                points,
                currentPoints,
                currentPoints - points,
                source,
                sourceId,
                description
        );
        
        // 保存积分记录
        UserPointsLog savedUserPointsLog = userPointsLogRepository.save(userPointsLog);
        
        // 更新用户积分
        Optional<Member> memberOpt = memberDomainService.getMember(new UserId(userId));
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            memberDomainService.usePoints(member.getId(), points);
        } else {
            throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
        }
        
        return userPointsLogAssembler.toDTO(savedUserPointsLog);
    }

    @Override
    @Transactional
    public UserPointsLogDTO adjustPoints(Long userId, Integer points, String description) {
        // 获取用户当前积分
        Integer currentPoints = getTotalPoints(userId);
        
        // 创建积分记录
        UserPointsLog userPointsLog;
        Optional<Member> memberOpt = memberDomainService.getMember(new UserId(userId));
        Member member;
        
        if (memberOpt.isEmpty()) {
            if (points <= 0) {
                throw new BusinessException(ResultCode.MEMBER_NOT_FOUND);
            }
            // 如果用户不是会员且要增加积分，创建会员
            member = memberDomainService.createMember(new UserId(userId), 1, false, null);
        } else {
            member = memberOpt.get();
        }
        
        MemberId memberId = member.getId();
        
        if (points >= 0) {
            // 增加积分
            userPointsLog = UserPointsLog.createGainLog(
                    new UserId(userId),
                    points,
                    currentPoints,
                    currentPoints + points,
                    5, // 系统调整
                    null,
                    description
            );
            
            // 更新用户积分
            memberDomainService.addPoints(memberId, points);
        } else {
            // 扣减积分
            int absPoints = Math.abs(points);
            
            // 检查积分是否足够
            if (currentPoints < absPoints) {
                throw new BusinessException(ResultCode.POINTS_NOT_ENOUGH);
            }
            
            userPointsLog = UserPointsLog.createUseLog(
                    new UserId(userId),
                    absPoints,
                    currentPoints,
                    currentPoints - absPoints,
                    5, // 系统调整
                    null,
                    description
            );
            
            // 更新用户积分
            memberDomainService.usePoints(memberId, absPoints);
        }
        
        // 保存积分记录
        UserPointsLog savedUserPointsLog = userPointsLogRepository.save(userPointsLog);
        
        return userPointsLogAssembler.toDTO(savedUserPointsLog);
    }

    @Override
    public Integer getTotalPoints(Long userId) {
        return memberDomainService.getMember(new UserId(userId))
                .map(member -> member.getPoints())
                .orElse(0);
    }

    @Override
    public Integer getAvailablePoints(Long userId) {
        return memberDomainService.getMember(new UserId(userId))
                .map(member -> member.getPoints() - member.getUsedPoints())
                .orElse(0);
    }

    @Override
    public boolean isPointsEnough(Long userId, Integer points) {
        Integer availablePoints = getAvailablePoints(userId);
        return availablePoints >= points;
    }
}