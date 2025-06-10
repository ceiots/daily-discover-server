package com.example.user.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.infrastructure.persistence.entity.UserAccountLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户账户流水Mapper接口
 */
@Mapper
public interface UserAccountLogMapper extends BaseMapper<UserAccountLogEntity> {
} 