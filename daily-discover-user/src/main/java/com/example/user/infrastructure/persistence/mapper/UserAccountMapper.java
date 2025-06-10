package com.example.user.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.infrastructure.persistence.entity.UserAccountEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户账户Mapper接口
 */
@Mapper
public interface UserAccountMapper extends BaseMapper<UserAccountEntity> {
} 