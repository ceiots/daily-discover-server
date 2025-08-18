package com.dailydiscover.mapper;

import com.dailydiscover.model.MottoEntity;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface MottoMapper {
    
    // 查找今日格言
    @Select("SELECT * FROM daily_mottos WHERE date = #{date} AND is_active = true ORDER BY created_at DESC LIMIT 1")
    MottoEntity findTodayMotto(@Param("date") String date);
    
    // 查找所有格言
    @Select("SELECT * FROM daily_mottos ORDER BY created_at DESC")
    List<MottoEntity> findAllMottos();
    
    // 根据ID查找格言
    @Select("SELECT * FROM daily_mottos WHERE id = #{id}")
    MottoEntity findById(@Param("id") Long id);
    
    // 根据日期查找格言
    @Select("SELECT * FROM daily_mottos WHERE date = #{date} ORDER BY created_at DESC")
    List<MottoEntity> findByDate(@Param("date") String date);
    
    // 查找所有活跃格言
    @Select("SELECT * FROM daily_mottos WHERE is_active = true ORDER BY created_at DESC")
    List<MottoEntity> findActiveMottos();
    
    // 插入格言
    @Insert("INSERT INTO daily_mottos (text, date, is_active, created_at, updated_at) " +
            "VALUES (#{text}, #{date}, #{isActive}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(MottoEntity motto);
    
    // 更新格言
    @Update("UPDATE daily_mottos SET text = #{text}, date = #{date}, is_active = #{isActive}, " +
            "updated_at = #{updatedAt} WHERE id = #{id}")
    int update(MottoEntity motto);
    
    // 删除格言
    @Delete("DELETE FROM daily_mottos WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    // 软删除格言
    @Update("UPDATE daily_mottos SET is_active = false, updated_at = NOW() WHERE id = #{id}")
    int softDeleteById(@Param("id") Long id);
}