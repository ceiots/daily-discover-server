package com.dailydiscover.mapper;

import com.dailydiscover.model.PaymentMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
    
    /**
     * 根据支付方式代码查询
     */
    @Select("SELECT * FROM payment_methods WHERE payment_code = #{methodCode}")
    PaymentMethod getByMethodCode(@Param("methodCode") String methodCode);
    
    /**
     * 查询可用的支付方式
     */
    @Select("SELECT * FROM payment_methods WHERE status = 'active' ORDER BY sort_order ASC")
    List<PaymentMethod> getAvailableMethods();
    
    /**
     * 根据支付方式类型查询
     */
    @Select("SELECT * FROM payment_methods WHERE payment_type = #{methodType} AND status = 'active' ORDER BY sort_order ASC")
    List<PaymentMethod> getByMethodType(@Param("methodType") String methodType);
    
    /**
     * 启用/禁用支付方式
     */
    @Update("UPDATE payment_methods SET status = CASE WHEN #{enabled} = true THEN 'active' ELSE 'inactive' END, updated_at = NOW() WHERE id = #{methodId}")
    int toggleMethodEnabled(@Param("methodId") Long methodId, @Param("enabled") Boolean enabled);
    
    /**
     * 更新支付方式配置
     */
    @Update("UPDATE payment_methods SET config_json = #{configJson}, updated_at = NOW() WHERE id = #{methodId}")
    int updateMethodConfig(@Param("methodId") Long methodId, @Param("configJson") String configJson);
    
    /**
     * 获取支持的支付方式类型
     */
    @Select("SELECT DISTINCT payment_type FROM payment_methods WHERE status = 'active'")
    List<String> getSupportedMethodTypes();
    
    /**
     * 验证支付方式是否可用
     */
    @Select("SELECT COUNT(*) > 0 FROM payment_methods WHERE payment_code = #{methodCode} AND status = 'active'")
    boolean validateMethodAvailability(@Param("methodCode") String methodCode);
}