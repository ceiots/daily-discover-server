package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.entity.LoginAttempt;
import org.apache.ibatis.annotations.Mapper;

/**
 * 登录尝试记录数据访问接口
 */
@Mapper
public interface LoginAttemptMapper extends BaseMapper<LoginAttempt> {
}