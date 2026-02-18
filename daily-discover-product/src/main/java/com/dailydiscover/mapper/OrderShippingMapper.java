package com.dailydiscover.mapper;

import com.dailydiscover.model.OrderShipping;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 订单物流信息表 Mapper
 */
@Mapper
public interface OrderShippingMapper extends BaseMapper<OrderShipping> {
    
    /**
     * 根据订单ID查询物流信息
     */
    @Select("SELECT * FROM order_shipping WHERE order_id = #{orderId}")
    OrderShipping findByOrderId(@Param("orderId") Long orderId);
}