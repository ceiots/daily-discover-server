package com.example.user.infrastructure.persistence.converter;

import com.example.user.domain.model.id.MemberId;
import com.example.user.domain.model.id.UserId;
import com.example.user.domain.model.member.Member;
import com.example.user.infrastructure.persistence.entity.MemberEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 会员实体转换器
 */
@Component
public class MemberConverter {

    /**
     * 将领域模型转换为实体
     *
     * @param member 会员领域模型
     * @return 会员实体
     */
    public MemberEntity toEntity(Member member) {
        if (member == null) {
            return null;
        }
        
        MemberEntity entity = new MemberEntity();
        
        if (member.getId() != null) {
            entity.setId(member.getId().getValue());
        }
        
        if (member.getUserId() != null) {
            entity.setUserId(member.getUserId().getValue());
        }
        
        entity.setMemberLevel(member.getMemberLevel());
        entity.setGrowthValue(member.getGrowthValue());
        entity.setPoints(member.getPoints());
        entity.setUsedPoints(member.getUsedPoints());
        entity.setIsForever(member.getIsForever());
        entity.setStartTime(member.getStartTime());
        entity.setEndTime(member.getEndTime());
        entity.setStatus(member.getStatus());
        entity.setFreeShippingCount(member.getFreeShippingCount());
        entity.setFreeReturnCount(member.getFreeReturnCount());
        entity.setCreateTime(member.getCreateTime());
        entity.setUpdateTime(member.getUpdateTime());
        
        return entity;
    }

    /**
     * 将实体转换为领域模型
     *
     * @param entity 会员实体
     * @return 会员领域模型
     */
    public Member toDomain(MemberEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Member member = Member.create(
            new UserId(entity.getUserId()),
            entity.getMemberLevel(),
            entity.getIsForever(),
            null
        );
        
        member.setId(new MemberId(entity.getId()));
        
        try {
            if (entity.getGrowthValue() > 0) {
                member.addGrowthValue(entity.getGrowthValue());
            }
            
            if (entity.getPoints() > 0) {
                member.addPoints(entity.getPoints());
            }
            
            if (entity.getFreeShippingCount() > 0) {
                member.addFreeShippingCount(entity.getFreeShippingCount());
            }
            
            if (entity.getFreeReturnCount() > 0) {
                member.addFreeReturnCount(entity.getFreeReturnCount());
            }
            
            if (entity.getStatus() == 0) {
                member.disable();
            } else {
                member.enable();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return member;
    }

    /**
     * 将实体列表转换为领域模型列表
     *
     * @param entities 会员实体列表
     * @return 会员领域模型列表
     */
    public List<Member> toDomainList(List<MemberEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }
} 