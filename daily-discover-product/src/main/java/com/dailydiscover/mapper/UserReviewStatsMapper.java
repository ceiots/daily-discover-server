package com.dailydiscover.mapper;

import com.dailydiscover.model.UserReviewStats;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户评价统计表 Mapper
 */
@Mapper
public interface UserReviewStatsMapper extends BaseMapper<UserReviewStats> {
}