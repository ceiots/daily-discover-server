package com.example.service;

import com.example.mapper.DeliveryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class DeliveryService {

    @Autowired
    private DeliveryMapper deliveryMapper;

    @Transactional
    public void updateDeliveryStatus(Long orderId, String status) {
        try {
            deliveryMapper.updateStatus(orderId, status);
        } catch (Exception e) {
            // 记录异常日志
            System.err.println("Error updating delivery status: " + e.getMessage());
            throw e; // 重新抛出异常以便上层处理
        }
    }

} 