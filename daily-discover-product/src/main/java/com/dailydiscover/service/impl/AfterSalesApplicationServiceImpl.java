package com.dailydiscover.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dailydiscover.mapper.AfterSalesApplicationMapper;
import com.dailydiscover.model.AfterSalesApplication;
import com.dailydiscover.service.AfterSalesApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AfterSalesApplicationServiceImpl extends ServiceImpl<AfterSalesApplicationMapper, AfterSalesApplication> implements AfterSalesApplicationService {
    
    @Autowired
    private AfterSalesApplicationMapper afterSalesApplicationMapper;
    
    @Override
    public List<AfterSalesApplication> findByOrderId(Long orderId) {
        return afterSalesApplicationMapper.findByOrderId(orderId);
    }
    
    @Override
    public List<AfterSalesApplication> findByUserId(Long userId) {
        return afterSalesApplicationMapper.findByUserId(userId);
    }
    
    @Override
    public List<AfterSalesApplication> findByStatus(String status) {
        return afterSalesApplicationMapper.findByStatus(status);
    }
    
    @Override
    public List<AfterSalesApplication> findByType(String afterSalesType) {
        return afterSalesApplicationMapper.findByType(afterSalesType);
    }
    
    @Override
    public List<AfterSalesApplication> findPendingApplications() {
        return afterSalesApplicationMapper.findPendingApplications();
    }
    
    @Override
    public AfterSalesApplication findByApplicationNo(String applicationNo) {
        return afterSalesApplicationMapper.findByApplicationNo(applicationNo);
    }
}