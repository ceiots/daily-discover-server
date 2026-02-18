package com.dailydiscover.mapper;

import com.dailydiscover.model.AfterSalesApplication;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 售后申请表 Mapper
 */
@Mapper
public interface AfterSalesApplicationMapper extends BaseMapper<AfterSalesApplication> {
    
    /**
     * 根据订单ID查询售后申请
     */
    @Select("SELECT * FROM after_sales_applications WHERE order_id = #{orderId} ORDER BY applied_at DESC")
    List<AfterSalesApplication> findByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据用户ID查询售后申请
     */
    @Select("SELECT * FROM after_sales_applications WHERE user_id = #{userId} ORDER BY applied_at DESC")
    List<AfterSalesApplication> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据售后状态查询申请
     */
    @Select("SELECT * FROM after_sales_applications WHERE status = #{status} ORDER BY applied_at DESC")
    List<AfterSalesApplication> findByStatus(@Param("status") String status);
    
    /**
     * 根据售后类型查询申请
     */
    @Select("SELECT * FROM after_sales_applications WHERE after_sales_type = #{afterSalesType} ORDER BY applied_at DESC")
    List<AfterSalesApplication> findByType(@Param("afterSalesType") String afterSalesType);
    
    /**
     * 查询待处理的售后申请
     */
    @Select("SELECT * FROM after_sales_applications WHERE status = 'pending' ORDER BY applied_at ASC")
    List<AfterSalesApplication> findPendingApplications();
    
    /**
     * 根据申请单号查询申请
     */
    @Select("SELECT * FROM after_sales_applications WHERE application_no = #{applicationNo}")
    AfterSalesApplication findByApplicationNo(@Param("applicationNo") String applicationNo);
}