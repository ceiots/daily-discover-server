package com.dailydiscover.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.user.entity.UserLoginRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户登录记录Mapper接口
 */
@Mapper
public interface UserLoginRecordMapper extends BaseMapper<UserLoginRecord> {
}