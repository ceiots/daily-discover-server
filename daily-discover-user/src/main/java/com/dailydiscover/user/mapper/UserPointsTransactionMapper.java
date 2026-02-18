package com.dailydiscover.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.user.entity.UserPointsTransaction;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户积分交易记录Mapper接口
 */
@Mapper
public interface UserPointsTransactionMapper extends BaseMapper<UserPointsTransaction> {
}