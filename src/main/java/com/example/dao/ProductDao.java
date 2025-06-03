package com.example.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.example.model.Product;

@Mapper
public interface ProductDao {
    
    /**
     * 根据类别ID和审核状态获取最新商品
     */
    @Select("SELECT * FROM recommendations WHERE category_id = #{categoryId} AND audit_status = #{auditStatus} AND deleted = #{deleted} ORDER BY created_at DESC LIMIT #{limit}")
    List<Product> findByCategoryIdAndAuditStatusOrderByCreatedAtDesc(
        @Param("categoryId") Long categoryId, 
        @Param("auditStatus") Integer auditStatus, 
        @Param("deleted") Integer deleted, 
        @Param("limit") Integer limit);
    
    /**
     * 根据审核状态获取销量最高的商品
     */
    @Select("SELECT * FROM recommendations WHERE audit_status = #{auditStatus} AND deleted = #{deleted} ORDER BY soldCount DESC LIMIT #{limit}")
    List<Product> findByAuditStatusOrderBySoldCountDesc(
        @Param("auditStatus") Integer auditStatus, 
        @Param("deleted") Integer deleted, 
        @Param("limit") Integer limit);
    
    /**
     * 根据审核状态获取最新商品
     */
    @Select("SELECT * FROM recommendations WHERE audit_status = #{auditStatus} AND deleted = #{deleted} ORDER BY created_at DESC LIMIT #{limit}")
    List<Product> findByAuditStatusOrderByCreatedAtDesc(
        @Param("auditStatus") Integer auditStatus, 
        @Param("deleted") Integer deleted, 
        @Param("limit") Integer limit);
    
    // 其他方法...
} 