package com.example.user.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.model.PageRequest;
import com.example.common.model.PageResult;
import com.example.user.domain.model.UserPointsLog;
import com.example.user.domain.model.id.MemberId;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.member.Member;
import com.example.user.domain.model.member.MemberLevel;
import com.example.user.domain.repository.MemberRepository;
import com.example.user.infrastructure.persistence.converter.MemberConverter;
import com.example.user.infrastructure.persistence.converter.MemberLevelConverter;
import com.example.user.infrastructure.persistence.converter.UserPointsLogConverter;
import com.example.user.infrastructure.persistence.entity.MemberEntity;
import com.example.user.infrastructure.persistence.entity.MemberLevelEntity;
import com.example.user.infrastructure.persistence.entity.UserPointsLogEntity;
import com.example.user.infrastructure.persistence.mapper.MemberLevelMapper;
import com.example.user.infrastructure.persistence.mapper.MemberMapper;
import com.example.user.infrastructure.persistence.mapper.UserPointsLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 会员仓储实现类
 */
@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberMapper memberMapper;
    private final MemberLevelMapper memberLevelMapper;
    private final UserPointsLogMapper userPointsLogMapper;
    private final MemberConverter memberConverter;
    private final MemberLevelConverter memberLevelConverter;
    private final UserPointsLogConverter userPointsLogConverter;

    @Override
    public Optional<Member> findById(MemberId id) {
        MemberEntity entity = memberMapper.selectById(id.getValue());
        return Optional.ofNullable(entity).map(memberConverter::toDomain);
    }

    @Override
    public Optional<Member> findByUserId(UserId userId) {
        LambdaQueryWrapper<MemberEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberEntity::getUserId, userId.getValue());
        MemberEntity entity = memberMapper.selectOne(wrapper);
        return Optional.ofNullable(entity).map(memberConverter::toDomain);
    }

    @Override
    public List<Member> findByMemberLevel(Integer memberLevel) {
        LambdaQueryWrapper<MemberEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberEntity::getMemberLevel, memberLevel);
        List<MemberEntity> entities = memberMapper.selectList(wrapper);
        return entities.stream().map(memberConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Member> findByStatus(Integer status) {
        LambdaQueryWrapper<MemberEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberEntity::getStatus, status);
        List<MemberEntity> entities = memberMapper.selectList(wrapper);
        return entities.stream().map(memberConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Member> findExpiredMembers(LocalDateTime expiryDate) {
        LambdaQueryWrapper<MemberEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberEntity::getIsForever, false)
                .le(MemberEntity::getEndTime, expiryDate)
                .eq(MemberEntity::getStatus, 1); // 只查询状态为正常的会员
        List<MemberEntity> entities = memberMapper.selectList(wrapper);
        return entities.stream().map(memberConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public PageResult<Member> findPage(PageRequest pageRequest, Integer status, Integer memberLevel) {
        LambdaQueryWrapper<MemberEntity> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null) {
            wrapper.eq(MemberEntity::getStatus, status);
        }
        
        if (memberLevel != null) {
            wrapper.eq(MemberEntity::getMemberLevel, memberLevel);
        }
        
        wrapper.orderByDesc(MemberEntity::getCreateTime);
        
        Page<MemberEntity> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        IPage<MemberEntity> entityPage = memberMapper.selectPage(page, wrapper);
        
        List<Member> members = entityPage.getRecords().stream()
                .map(memberConverter::toDomain)
                .collect(Collectors.toList());
        
        return new PageResult<>(pageRequest.getPageNum(), pageRequest.getPageSize(), 
                entityPage.getTotal(), members);
    }

    @Override
    public Member save(Member member) {
        MemberEntity entity = memberConverter.toEntity(member);
        memberMapper.insert(entity);
        member.setId(new MemberId(entity.getId()));
        return member;
    }

    @Override
    public Member update(Member member) {
        MemberEntity entity = memberConverter.toEntity(member);
        memberMapper.updateById(entity);
        return member;
    }

    @Override
    public boolean delete(MemberId id) {
        return memberMapper.deleteById(id.getValue()) > 0;
    }

    @Override
    public boolean updateStatus(MemberId id, Integer status) {
        LambdaUpdateWrapper<MemberEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MemberEntity::getId, id.getValue())
                .set(MemberEntity::getStatus, status);
        return memberMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean updateMemberLevel(UserId userId, Integer memberLevel) {
        LambdaUpdateWrapper<MemberEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MemberEntity::getUserId, userId.getValue())
                .set(MemberEntity::getMemberLevel, memberLevel);
        return memberMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean updatePoints(UserId userId, Integer points) {
        LambdaUpdateWrapper<MemberEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MemberEntity::getUserId, userId.getValue())
                .setSql("points = points + " + points);
        return memberMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean updateGrowthValue(UserId userId, Integer growthValue) {
        LambdaUpdateWrapper<MemberEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MemberEntity::getUserId, userId.getValue())
                .setSql("growth_value = growth_value + " + growthValue);
        return memberMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean updateUsedPoints(UserId userId, Integer points) {
        LambdaUpdateWrapper<MemberEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MemberEntity::getUserId, userId.getValue())
                .setSql("used_points = used_points + " + points);
        return memberMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean updateFreeShippingCount(UserId userId, Integer count) {
        LambdaUpdateWrapper<MemberEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MemberEntity::getUserId, userId.getValue())
                .setSql("free_shipping_count = free_shipping_count + " + count);
        return memberMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean updateFreeReturnCount(UserId userId, Integer count) {
        LambdaUpdateWrapper<MemberEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MemberEntity::getUserId, userId.getValue())
                .setSql("free_return_count = free_return_count + " + count);
        return memberMapper.update(null, wrapper) > 0;
    }

    @Override
    public boolean extendMembership(UserId userId, LocalDateTime endTime) {
        LambdaUpdateWrapper<MemberEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(MemberEntity::getUserId, userId.getValue())
                .set(MemberEntity::getEndTime, endTime);
        return memberMapper.update(null, wrapper) > 0;
    }

    @Override
    public Optional<MemberLevel> findMemberLevelById(Long id) {
        MemberLevelEntity entity = memberLevelMapper.selectById(id);
        return Optional.ofNullable(entity).map(memberLevelConverter::toDomain);
    }

    @Override
    public Optional<MemberLevel> findMemberLevelByLevel(Integer level) {
        LambdaQueryWrapper<MemberLevelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberLevelEntity::getLevel, level);
        MemberLevelEntity entity = memberLevelMapper.selectOne(wrapper);
        return Optional.ofNullable(entity).map(memberLevelConverter::toDomain);
    }

    @Override
    public List<MemberLevel> findAllMemberLevels() {
        LambdaQueryWrapper<MemberLevelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(MemberLevelEntity::getLevel);
        List<MemberLevelEntity> entities = memberLevelMapper.selectList(wrapper);
        return entities.stream().map(memberLevelConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public MemberLevel saveMemberLevel(MemberLevel memberLevel) {
        MemberLevelEntity entity = memberLevelConverter.toEntity(memberLevel);
        memberLevelMapper.insert(entity);
        // 设置ID并返回
        memberLevel.setId(entity.getId());
        return memberLevel;
    }

    @Override
    public MemberLevel updateMemberLevel(MemberLevel memberLevel) {
        MemberLevelEntity entity = memberLevelConverter.toEntity(memberLevel);
        memberLevelMapper.updateById(entity);
        return memberLevel;
    }

    @Override
    public boolean deleteMemberLevel(Long id) {
        return memberLevelMapper.deleteById(id) > 0;
    }

    @Override
    public Optional<MemberLevel> findMemberLevelByGrowthValue(Integer growthValue) {
        LambdaQueryWrapper<MemberLevelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(MemberLevelEntity::getGrowthMin, growthValue)
                .ge(MemberLevelEntity::getGrowthMax, growthValue)
                .eq(MemberLevelEntity::getStatus, 1); // 只查询启用状态的会员等级
        MemberLevelEntity entity = memberLevelMapper.selectOne(wrapper);
        return Optional.ofNullable(entity).map(memberLevelConverter::toDomain);
    }

    @Override
    public int countByMemberLevel(Integer memberLevel) {
        LambdaQueryWrapper<MemberEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberEntity::getMemberLevel, memberLevel);
        return Math.toIntExact(memberMapper.selectCount(wrapper));
    }

    @Override
    public Integer findLevelNumberByGrowthValue(Integer growthValue) {
        Optional<MemberLevel> levelOpt = findMemberLevelByGrowthValue(growthValue);
        return levelOpt.map(MemberLevel::getLevel).orElse(1); // 默认返回1级
    }

    @Override
    public boolean existsByUserId(UserId userId) {
        LambdaQueryWrapper<MemberEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberEntity::getUserId, userId.getValue());
        return memberMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<MemberLevel> findAllLevels() {
        return findAllMemberLevels();
    }

    @Override
    public Optional<MemberLevel> findLevelByLevel(Integer level) {
        return findMemberLevelByLevel(level);
    }

    @Override
    public Optional<MemberLevel> findLevelById(Long id) {
        return findMemberLevelById(id);
    }

    @Override
    public Optional<MemberLevel> findLevelByGrowthValue(Integer growthValue) {
        return findMemberLevelByGrowthValue(growthValue);
    }

    @Override
    public MemberLevel saveLevel(MemberLevel memberLevel) {
        return saveMemberLevel(memberLevel);
    }

    @Override
    public MemberLevel updateLevel(MemberLevel memberLevel) {
        return updateMemberLevel(memberLevel);
    }

    @Override
    public boolean deleteLevel(Long id) {
        return deleteMemberLevel(id);
    }

    @Override
    public UserPointsLog savePointsLog(UserPointsLog pointsLog) {
        UserPointsLogEntity entity = userPointsLogConverter.toEntity(pointsLog);
        userPointsLogMapper.insert(entity);
        pointsLog.setId(entity.getId());
        return pointsLog;
    }

    @Override
    public PageResult<UserPointsLog> getPointsLogsByUserId(UserId userId, PageRequest pageRequest) {
        LambdaQueryWrapper<UserPointsLogEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPointsLogEntity::getUserId, userId.getValue())
                .orderByDesc(UserPointsLogEntity::getCreateTime);
        
        Page<UserPointsLogEntity> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        IPage<UserPointsLogEntity> entityPage = userPointsLogMapper.selectPage(page, wrapper);
        
        List<UserPointsLog> logs = userPointsLogConverter.toDomainList(entityPage.getRecords());
        
        return new PageResult<>(pageRequest.getPageNum(), pageRequest.getPageSize(), 
                entityPage.getTotal(), logs);
    }
} 