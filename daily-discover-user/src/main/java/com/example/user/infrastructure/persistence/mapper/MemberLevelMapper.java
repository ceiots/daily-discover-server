package com.example.user.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.infrastructure.persistence.entity.MemberLevelEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员等级Mapper接口
 */
@Mapper
public interface MemberLevelMapper extends BaseMapper<MemberLevelEntity> {
} 