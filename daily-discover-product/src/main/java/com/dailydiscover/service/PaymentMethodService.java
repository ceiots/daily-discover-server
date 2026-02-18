package com.dailydiscover.service;

import com.dailydiscover.model.PaymentMethod;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 支付方式服务接口
 */
public interface PaymentMethodService extends IService<PaymentMethod> {
    
    /**
     * 根据支付方式代码查询
     */
    PaymentMethod getByMethodCode(String methodCode);
    
    /**
     * 查询可用的支付方式
     */
    java.util.List<PaymentMethod> getAvailableMethods();
    
    /**
     * 根据支付方式类型查询
     */
    java.util.List<PaymentMethod> getByMethodType(String methodType);
    
    /**
     * 启用/禁用支付方式
     */
    boolean toggleMethodEnabled(Long methodId, Boolean enabled);
    
    /**
     * 更新支付方式配置
     */
    boolean updateMethodConfig(Long methodId, String configJson);
    
    /**
     * 获取支持的支付方式类型
     */
    java.util.List<String> getSupportedMethodTypes();
    
    /**
     * 验证支付方式是否可用
     */
    boolean validateMethodAvailability(String methodCode);
}