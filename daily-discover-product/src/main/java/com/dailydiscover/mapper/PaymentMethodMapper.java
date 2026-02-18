package com.dailydiscover.mapper;

import com.dailydiscover.model.PaymentMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 支付方式表 Mapper
 */
@Mapper
public interface PaymentMethodMapper extends BaseMapper<PaymentMethod> {
    
    /**
     * 查询所有启用的支付方式
     */
    @Select("SELECT * FROM payment_methods WHERE status = 'active' ORDER BY sort_order ASC")
    List<PaymentMethod> findActivePaymentMethods();
    
    /**
     * 根据支付方式类型查询
     */
    @Select("SELECT * FROM payment_methods WHERE payment_type = #{paymentType} AND status = 'active' ORDER BY sort_order ASC")
    List<PaymentMethod> findByPaymentType(@Param("paymentType") String paymentType);
    
    /**
     * 查询默认支付方式
     */
    @Select("SELECT * FROM payment_methods WHERE is_default = true AND status = 'active'")
    PaymentMethod findDefaultPaymentMethod();
    
    /**
     * 根据支付方式代码查询
     */
    @Select("SELECT * FROM payment_methods WHERE payment_code = #{paymentCode}")
    PaymentMethod findByPaymentCode(@Param("paymentCode") String paymentCode);
}