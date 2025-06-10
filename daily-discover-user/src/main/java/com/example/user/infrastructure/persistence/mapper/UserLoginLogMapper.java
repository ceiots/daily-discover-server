package com.example.user.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.infrastructure.persistence.entity.UserLoginLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户登录日志Mapper
 */
@Mapper
public interface UserLoginLogMapper extends BaseMapper<UserLoginLogEntity> {
}