package com.example.mapper;

import org.apache.ibatis.annotations.*;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.ProductAuditLog;

import java.util.List;

@Mapper
public interface ProductAuditLogMapper extends BaseMapper<ProductAuditLog> {

    @Insert("INSERT INTO product_audit_log(product_id, audit_status, audit_remark, auditor_id, audit_time) " +
            "VALUES(#{productId}, #{auditStatus}, #{auditRemark}, #{auditorId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductAuditLog auditLog);
    
    @Select("SELECT pal.*, u.username as auditorName, p.title as productTitle " +
            "FROM product_audit_log pal " +
            "LEFT JOIN user u ON pal.auditor_id = u.id " +
            "LEFT JOIN product p ON pal.product_id = p.id " +
            "WHERE pal.id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "auditStatus", column = "audit_status"),
        @Result(property = "auditRemark", column = "audit_remark"),
        @Result(property = "auditorId", column = "auditor_id"),
        @Result(property = "auditTime", column = "audit_time"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "auditorName", column = "auditorName"),
        @Result(property = "productTitle", column = "productTitle")
    })
    ProductAuditLog findById(@Param("id") Long id);
    
    @Select("SELECT pal.*, u.username as auditorName, p.title as productTitle " +
            "FROM product_audit_log pal " +
            "LEFT JOIN user u ON pal.auditor_id = u.id " +
            "LEFT JOIN product p ON pal.product_id = p.id " +
            "WHERE pal.product_id = #{productId} " +
            "ORDER BY pal.audit_time DESC")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "auditStatus", column = "audit_status"),
        @Result(property = "auditRemark", column = "audit_remark"),
        @Result(property = "auditorId", column = "auditor_id"),
        @Result(property = "auditTime", column = "audit_time"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "auditorName", column = "auditorName"),
        @Result(property = "productTitle", column = "productTitle")
    })
    List<ProductAuditLog> findByProductId(@Param("productId") Long productId);
    
    @Select("SELECT pal.*, u.username as auditorName, p.title as productTitle " +
            "FROM product_audit_log pal " +
            "LEFT JOIN user u ON pal.auditor_id = u.id " +
            "LEFT JOIN product p ON pal.product_id = p.id " +
            "WHERE pal.auditor_id = #{auditorId} " +
            "ORDER BY pal.audit_time DESC " +
            "LIMIT #{limit}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "auditStatus", column = "audit_status"),
        @Result(property = "auditRemark", column = "audit_remark"),
        @Result(property = "auditorId", column = "auditor_id"),
        @Result(property = "auditTime", column = "audit_time"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "auditorName", column = "auditorName"),
        @Result(property = "productTitle", column = "productTitle")
    })
    List<ProductAuditLog> findByAuditorId(@Param("auditorId") Long auditorId, @Param("limit") Integer limit);
    
    @Select("SELECT pal.*, u.username as auditorName, p.title as productTitle " +
            "FROM product_audit_log pal " +
            "LEFT JOIN user u ON pal.auditor_id = u.id " +
            "LEFT JOIN product p ON pal.product_id = p.id " +
            "ORDER BY pal.audit_time DESC " +
            "LIMIT #{limit} OFFSET #{offset}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "auditStatus", column = "audit_status"),
        @Result(property = "auditRemark", column = "audit_remark"),
        @Result(property = "auditorId", column = "auditor_id"),
        @Result(property = "auditTime", column = "audit_time"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "auditorName", column = "auditorName"),
        @Result(property = "productTitle", column = "productTitle")
    })
    List<ProductAuditLog> findAll(@Param("limit") Integer limit, @Param("offset") Integer offset);
    
    @Select("SELECT COUNT(*) FROM product_audit_log")
    int count();
} 