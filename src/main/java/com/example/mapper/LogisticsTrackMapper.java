package com.example.mapper;

import com.example.model.LogisticsTrack;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 物流轨迹Mapper接口
 */
@Mapper
public interface LogisticsTrackMapper {
    
    /**
     * 根据ID查询物流轨迹
     */
    @Select("SELECT * FROM logistics_track WHERE id = #{id}")
    LogisticsTrack findById(Long id);
    
    /**
     * 根据物流信息ID查询物流轨迹列表
     */
    @Select("SELECT * FROM logistics_track WHERE logistics_id = #{logisticsId} ORDER BY track_time DESC")
    List<LogisticsTrack> findByLogisticsId(Long logisticsId);
    
    /**
     * 插入物流轨迹
     */
    @Insert("INSERT INTO logistics_track (logistics_id, track_time, location, description, status, status_code, operator, operator_phone) " +
            "VALUES (#{logisticsId}, #{trackTime}, #{location}, #{description}, #{status}, #{statusCode}, #{operator}, #{operatorPhone})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(LogisticsTrack logisticsTrack);
    
    /**
     * 批量插入物流轨迹
     */
    @Insert("<script>" +
            "INSERT INTO logistics_track (logistics_id, track_time, location, description, status, status_code, operator, operator_phone) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.logisticsId}, #{item.trackTime}, #{item.location}, #{item.description}, #{item.status}, #{item.statusCode}, #{item.operator}, #{item.operatorPhone})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("list") List<LogisticsTrack> logisticsTracks);
    
    /**
     * 删除物流轨迹
     */
    @Delete("DELETE FROM logistics_track WHERE id = #{id}")
    int delete(Long id);
    
    /**
     * 根据物流信息ID删除物流轨迹
     */
    @Delete("DELETE FROM logistics_track WHERE logistics_id = #{logisticsId}")
    int deleteByLogisticsId(Long logisticsId);
} 