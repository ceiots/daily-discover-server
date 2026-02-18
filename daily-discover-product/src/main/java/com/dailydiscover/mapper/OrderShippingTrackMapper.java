package com.dailydiscover.mapper;

import com.dailydiscover.model.OrderShippingTrack;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 物流跟踪记录表 Mapper
 */
@Mapper
public interface OrderShippingTrackMapper extends BaseMapper<OrderShippingTrack> {
}