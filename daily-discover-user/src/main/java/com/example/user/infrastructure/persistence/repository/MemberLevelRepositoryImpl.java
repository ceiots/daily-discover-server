package com.example.user.infrastructure.persistence.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.user.domain.model.member.MemberLevel;
import com.example.user.domain.repository.MemberLevelRepository;
import com.example.user.infrastructure.persistence.converter.MemberLevelConverter;
import com.example.user.infrastructure.persistence.entity.MemberLevelEntity;
import com.example.user.infrastructure.persistence.mapper.MemberLevelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 会员等级仓储实现类
 */
@Repository
@RequiredArgsConstructor
public class MemberLevelRepositoryImpl implements MemberLevelRepository {

    private final MemberLevelMapper memberLevelMapper;
    private final MemberLevelConverter memberLevelConverter;

    @Override
    public Optional<MemberLevel> findById(Long id) {
        MemberLevelEntity entity = memberLevelMapper.selectById(id);
        return Optional.ofNullable(entity).map(memberLevelConverter::toDomain);
    }

    @Override
    public Optional<MemberLevel> findByLevel(Integer level) {
        LambdaQueryWrapper<MemberLevelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberLevelEntity::getLevel, level);
        MemberLevelEntity entity = memberLevelMapper.selectOne(wrapper);
        return Optional.ofNullable(entity).map(memberLevelConverter::toDomain);
    }

    @Override
    public Optional<MemberLevel> findByGrowthValue(Integer growthValue) {
        LambdaQueryWrapper<MemberLevelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(MemberLevelEntity::getGrowthMin, growthValue)
                .ge(MemberLevelEntity::getGrowthMax, growthValue)
                .eq(MemberLevelEntity::getStatus, 1); // 只查询启用状态的会员等级
        MemberLevelEntity entity = memberLevelMapper.selectOne(wrapper);
        return Optional.ofNullable(entity).map(memberLevelConverter::toDomain);
    }

    @Override
    public List<MemberLevel> findAll() {
        List<MemberLevelEntity> entities = memberLevelMapper.selectList(null);
        return entities.stream().map(memberLevelConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<MemberLevel> findAllEnabled() {
        LambdaQueryWrapper<MemberLevelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberLevelEntity::getStatus, 1);
        List<MemberLevelEntity> entities = memberLevelMapper.selectList(wrapper);
        return entities.stream().map(memberLevelConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public MemberLevel save(MemberLevel memberLevel) {
        MemberLevelEntity entity = memberLevelConverter.toEntity(memberLevel);
        memberLevelMapper.insert(entity);
        memberLevel.setId(entity.getId());
        return memberLevel;
    }

    @Override
    public MemberLevel update(MemberLevel memberLevel) {
        MemberLevelEntity entity = memberLevelConverter.toEntity(memberLevel);
        memberLevelMapper.updateById(entity);
        return memberLevel;
    }

    @Override
    public boolean delete(Long id) {
        return memberLevelMapper.deleteById(id) > 0;
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        MemberLevelEntity entity = new MemberLevelEntity();
        entity.setId(id);
        entity.setStatus(status);
        return memberLevelMapper.updateById(entity) > 0;
    }

    @Override
    public boolean existsByLevel(Integer level) {
        LambdaQueryWrapper<MemberLevelEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberLevelEntity::getLevel, level);
        return memberLevelMapper.selectCount(wrapper) > 0;
    }
}