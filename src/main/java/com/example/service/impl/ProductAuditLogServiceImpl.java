package com.example.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.common.exception.ApiException;
import com.example.mapper.ProductAuditLogMapper;
import com.example.mapper.ProductMapper;
import com.example.model.ProductAuditLog;
import com.example.service.ProductAuditLogService;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ProductAuditLogServiceImpl implements ProductAuditLogService {

    @Autowired
    private ProductAuditLogMapper auditLogMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Override
    @Transactional
    public ProductAuditLog create(ProductAuditLog auditLog) {
        try {
            auditLogMapper.insert(auditLog);
            return auditLogMapper.findById(auditLog.getId());
        } catch (Exception e) {
            log.error("创建商品审核记录失败", e);
            throw new ApiException("创建商品审核记录失败: " + e.getMessage());
        }
    }
    
    @Override
    public ProductAuditLog getById(Long id) {
        return auditLogMapper.findById(id);
    }
    
    @Override
    public List<ProductAuditLog> getByProductId(Long productId) {
        return auditLogMapper.findByProductId(productId);
    }
    
    @Override
    public List<ProductAuditLog> getByAuditorId(Long auditorId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 20; // 默认限制20条记录
        }
        return auditLogMapper.findByAuditorId(auditorId, limit);
    }
    
    @Override
    public Map<String, Object> getAll(Integer page, Integer size) {
        if (page == null || page < 0) {
            page = 0;
        }
        
        if (size == null || size <= 0) {
            size = 10;
        }
        
        Map<String, Object> result = new HashMap<>();
        try {
            List<ProductAuditLog> logs = auditLogMapper.findAll(size, page * size);
            int total = auditLogMapper.count();
            
            result.put("logs", logs);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);
            
            return result;
        } catch (Exception e) {
            log.error("获取审核记录列表失败", e);
            throw new ApiException("获取审核记录列表失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public ProductAuditLog auditProduct(Long productId, Integer auditStatus, String auditRemark, Long auditorId) {
        try {
            // 更新商品审核状态
            productMapper.updateAuditStatus(productId, auditStatus, auditRemark);
            
            // 创建审核记录
            ProductAuditLog auditLog = ProductAuditLog.create(productId, auditStatus, auditRemark, auditorId);
            auditLogMapper.insert(auditLog);
            
            return auditLogMapper.findById(auditLog.getId());
        } catch (Exception e) {
            log.error("商品审核操作失败", e);
            throw new ApiException("商品审核操作失败: " + e.getMessage());
        }
    }
    
    @Override
    public ProductAuditLog getLatestByProductId(Long productId) {
        List<ProductAuditLog> logs = auditLogMapper.findByProductId(productId);
        if (logs != null && !logs.isEmpty()) {
            return logs.get(0); // 按时间倒序排列，第一条即为最新
        }
        return null;
    }
    
    @Override
    public Map<Integer, Integer> countByAuditStatus() {
        Map<Integer, Integer> result = new HashMap<>();
        try {
            // 这里需要针对商品表做统计，因为审核记录可能有多条
            // 假设有辅助方法获取商品审核状态统计
            result.put(0, productMapper.countByAuditStatus(0)); // 待审核
            result.put(1, productMapper.countByAuditStatus(1)); // 已通过
            result.put(2, productMapper.countByAuditStatus(2)); // 未通过
            
            return result;
        } catch (Exception e) {
            log.error("统计商品审核状态失败", e);
            // 返回空统计结果而不抛出异常
            result.put(0, 0);
            result.put(1, 0);
            result.put(2, 0);
            return result;
        }
    }
} 