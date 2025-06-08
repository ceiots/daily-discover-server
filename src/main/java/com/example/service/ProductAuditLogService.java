package com.example.service;

import org.springframework.transaction.annotation.Transactional;

import com.example.model.ProductAuditLog;

import java.util.List;
import java.util.Map;

public interface ProductAuditLogService {

    /**
     * 创建审核记录
     */
    @Transactional
    ProductAuditLog create(ProductAuditLog auditLog);
    
    /**
     * 根据ID获取审核记录
     */
    ProductAuditLog getById(Long id);
    
    /**
     * 根据商品ID获取审核记录列表
     */
    List<ProductAuditLog> getByProductId(Long productId);
    
    /**
     * 根据审核人ID获取审核记录列表
     */
    List<ProductAuditLog> getByAuditorId(Long auditorId, Integer limit);
    
    /**
     * 分页获取所有审核记录
     */
    Map<String, Object> getAll(Integer page, Integer size);
    
    /**
     * 商品审核操作
     * @param productId 商品ID
     * @param auditStatus 审核状态 (0:待审核, 1:已通过, 2:未通过)
     * @param auditRemark 审核备注
     * @param auditorId 审核人ID
     * @return 审核记录
     */
    @Transactional
    ProductAuditLog auditProduct(Long productId, Integer auditStatus, String auditRemark, Long auditorId);
    
    /**
     * 获取商品最新审核记录
     */
    ProductAuditLog getLatestByProductId(Long productId);
    
    /**
     * 统计不同审核状态的商品数量
     */
    Map<Integer, Integer> countByAuditStatus();
} 