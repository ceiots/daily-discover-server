package com.dailydiscover.mapper;

import com.dailydiscover.model.PaymentMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付方式表 Mapper
 */
@Mapper
public interface PaymentMethodMapper extends BaseMapper<PaymentMethod> {
}