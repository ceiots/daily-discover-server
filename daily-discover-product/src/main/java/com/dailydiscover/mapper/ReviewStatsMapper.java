package com.dailydiscover.mapper;

import com.dailydiscover.model.ReviewStats;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价统计表 Mapper
 */
@Mapper
public interface ReviewStatsMapper extends BaseMapper<ReviewStats> {
}