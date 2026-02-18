package com.dailydiscover.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.user.entity.UserToken;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户令牌Mapper接口
 */
@Mapper
public interface UserTokenMapper extends BaseMapper<UserToken> {
}