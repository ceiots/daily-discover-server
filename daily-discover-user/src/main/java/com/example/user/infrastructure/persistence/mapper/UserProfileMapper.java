package com.example.user.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.infrastructure.persistence.entity.UserProfileEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户详情Mapper接口
 */
@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfileEntity> {
} 