package com.example.user.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.infrastructure.persistence.entity.UserAuthEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户授权Mapper接口
 */
@Mapper
public interface UserAuthMapper extends BaseMapper<UserAuthEntity> {
} 