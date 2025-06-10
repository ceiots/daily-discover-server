package com.example.user.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.infrastructure.persistence.entity.UserPointsLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户积分记录Mapper接口
 */
@Mapper
public interface UserPointsLogMapper extends BaseMapper<UserPointsLogEntity> {
} 