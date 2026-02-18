package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.CustomerStats;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerStatsMapper extends BaseMapper<CustomerStats> {
}