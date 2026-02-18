package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.PaymentMethodMapper;
import com.dailydiscover.model.PaymentMethod;
import com.dailydiscover.service.PaymentMethodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PaymentMethodServiceImpl extends ServiceImpl<PaymentMethodMapper, PaymentMethod> implements PaymentMethodService {
    
    @Autowired
    private PaymentMethodMapper paymentMethodMapper;
    
    @Override
    public PaymentMethod getByMethodCode(String methodCode) {
        return paymentMethodMapper.getByMethodCode(methodCode);
    }
    
    @Override
    public List<PaymentMethod> getAvailableMethods() {
        return paymentMethodMapper.getAvailableMethods();
    }
    
    @Override
    public List<PaymentMethod> getByMethodType(String methodType) {
        return paymentMethodMapper.getByMethodType(methodType);
    }
    
    @Override
    public boolean toggleMethodEnabled(Long methodId, Boolean enabled) {
        return paymentMethodMapper.toggleMethodEnabled(methodId, enabled);
    }
    
    @Override
    public boolean updateMethodConfig(Long methodId, String configJson) {
        return paymentMethodMapper.updateMethodConfig(methodId, configJson);
    }
    
    @Override
    public List<String> getSupportedMethodTypes() {
        return paymentMethodMapper.getSupportedMethodTypes();
    }
    
    @Override
    public boolean validateMethodAvailability(String methodCode) {
        return paymentMethodMapper.validateMethodAvailability(methodCode);
    }
}