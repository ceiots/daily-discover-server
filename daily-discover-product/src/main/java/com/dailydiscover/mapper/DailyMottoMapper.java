package com.dailydiscover.mapper;

import com.dailydiscover.model.DailyMotto;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DailyMottoMapper {
    
    // 查找今日箴言
    @Select("SELECT * FROM daily_mottos WHERE date = CURDATE() AND is_active = true")
    List<DailyMotto> findTodayMottos();
    
    // 查找指定日期的箴言
    @Select("SELECT * FROM daily_mottos WHERE date = #{date} AND is_active = true")
    List<DailyMotto> findMottosByDate(@Param("date") LocalDate date);
    
    // 查找所有活跃箴言
    @Select("SELECT * FROM daily_mottos WHERE is_active = true ORDER BY date DESC")
    List<DailyMotto> findAllActiveMottos();
    
    // 根据ID查找箴言
    @Select("SELECT * FROM daily_mottos WHERE id = #{id}")
    DailyMotto findById(@Param("id") Long id);
    
    // 插入箴言
    @Insert("INSERT INTO daily_mottos (text, date, is_active, created_at, updated_at) " +
            "VALUES (#{text}, #{date}, #{isActive}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DailyMotto dailyMotto);
    
    // 更新箴言
    @Update("UPDATE daily_mottos SET text = #{text}, date = #{date}, is_active = #{isActive}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(DailyMotto dailyMotto);
    
    // 删除箴言
    @Delete("DELETE FROM daily_mottos WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    // 批量插入箴言
    @Insert("<script>" +
            "INSERT INTO daily_mottos (text, date, is_active, created_at, updated_at) VALUES " +
            "<foreach collection='mottos' item='motto' separator=','>" +
            "(#{motto.text}, #{motto.date}, #{motto.isActive}, #{motto.createdAt}, #{motto.updatedAt})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("mottos") List<DailyMotto> mottos);
}