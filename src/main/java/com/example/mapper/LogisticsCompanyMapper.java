package com.example.mapper;

import com.example.model.LogisticsCompany;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 物流公司Mapper接口
 */
@Mapper
public interface LogisticsCompanyMapper {
    
    /**
     * 根据ID查询物流公司
     */
    @Select("SELECT * FROM logistics_company WHERE id = #{id}")
    LogisticsCompany findById(Long id);
    
    /**
     * 根据编码查询物流公司
     */
    @Select("SELECT * FROM logistics_company WHERE code = #{code}")
    LogisticsCompany findByCode(String code);
    
    /**
     * 查询所有启用的物流公司
     */
    @Select("SELECT * FROM logistics_company WHERE enabled = 1 ORDER BY sort")
    List<LogisticsCompany> findAllEnabled();
    
    /**
     * 查询所有物流公司
     */
    @Select("SELECT * FROM logistics_company ORDER BY sort")
    List<LogisticsCompany> findAll();
    
    /**
     * 插入物流公司
     */
    @Insert("INSERT INTO logistics_company (code, name, short_name, phone, website, logo, enabled, sort, remark) " +
            "VALUES (#{code}, #{name}, #{shortName}, #{phone}, #{website}, #{logo}, #{enabled}, #{sort}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(LogisticsCompany logisticsCompany);
    
    /**
     * 更新物流公司
     */
    @Update("UPDATE logistics_company SET code = #{code}, name = #{name}, short_name = #{shortName}, " +
            "phone = #{phone}, website = #{website}, logo = #{logo}, enabled = #{enabled}, " +
            "sort = #{sort}, remark = #{remark} WHERE id = #{id}")
    int update(LogisticsCompany logisticsCompany);
    
    /**
     * 更新物流公司状态
     */
    @Update("UPDATE logistics_company SET enabled = #{enabled} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("enabled") Boolean enabled);
    
    /**
     * 删除物流公司
     */
    @Delete("DELETE FROM logistics_company WHERE id = #{id}")
    int delete(Long id);
} 