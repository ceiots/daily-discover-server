package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.OrdersCore;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单核心表 Mapper
 * 注意：实际订单表分为 orders_core 和 orders_extend 两个表
 * 此Mapper仅用于操作orders_core表
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrdersCore> {
}