package com.example.model;

import java.util.Date;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 商品审核记录实体类
 * 记录商品审核历史
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductAuditLog {
    private Long id;
    private Long productId;             // 商品ID
    private Integer auditStatus;        // 审核状态:0-待审核,1-已通过,2-未通过
    private String auditRemark;         // 审核备注
    private Long auditorId;             // 审核人ID
    private Date auditTime;             // 审核时间
    private Date createTime;            // 创建时间
    
    // 非持久化字段
    private String auditorName;         // 审核人姓名
    private String productTitle;        // 商品标题
    
    /**
     * 获取审核状态描述
     */
    public String getAuditStatusDesc() {
        if (auditStatus == null) {
            return "未知状态";
        }
        
        switch (auditStatus) {
            case 0: return "待审核";
            case 1: return "已通过";
            case 2: return "未通过";
            default: return "未知状态";
        }
    }
    
    /**
     * 创建审核记录
     */
    public static ProductAuditLog create(Long productId, Integer auditStatus, 
            String auditRemark, Long auditorId) {
        
        ProductAuditLog log = new ProductAuditLog();
        log.setProductId(productId);
        log.setAuditStatus(auditStatus);
        log.setAuditRemark(auditRemark);
        log.setAuditorId(auditorId);
        log.setAuditTime(new Date());
        log.setCreateTime(new Date());
        
        return log;
    }
} 