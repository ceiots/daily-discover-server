package com.dailydiscover.mapper;

import com.dailydiscover.model.DailyTheme;
import org.apache.ibatis.annotations.*;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface DailyThemeMapper {
    
    // 查找今日主题
    @Select("SELECT * FROM daily_themes WHERE theme_date = CURDATE() AND is_active = true")
    List<DailyTheme> findTodayThemes();
    
    // 查找昨日主题
    @Select("SELECT * FROM daily_themes WHERE theme_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY) AND is_active = true")
    List<DailyTheme> findYesterdayThemes();
    
    // 查找指定日期的主题
    @Select("SELECT * FROM daily_themes WHERE theme_date = #{date} AND is_active = true")
    List<DailyTheme> findThemesByDate(@Param("date") LocalDate date);
    
    // 根据主题类型查找主题
    @Select("SELECT * FROM daily_themes WHERE theme_type = #{themeType} AND is_active = true ORDER BY theme_date DESC")
    List<DailyTheme> findThemesByType(@Param("themeType") String themeType);
    
    // 查找所有活跃主题
    @Select("SELECT * FROM daily_themes WHERE is_active = true ORDER BY theme_date DESC")
    List<DailyTheme> findAllActiveThemes();
    
    // 根据ID查找主题
    @Select("SELECT * FROM daily_themes WHERE id = #{id}")
    DailyTheme findById(@Param("id") Long id);
    
    // 插入主题
    @Insert("INSERT INTO daily_themes (title, subtitle, image_url, theme_date, theme_type, is_active, created_at, updated_at) " +
            "VALUES (#{title}, #{subtitle}, #{imageUrl}, #{themeDate}, #{themeType}, #{isActive}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(DailyTheme dailyTheme);
    
    // 更新主题
    @Update("UPDATE daily_themes SET title = #{title}, subtitle = #{subtitle}, image_url = #{imageUrl}, " +
            "theme_date = #{themeDate}, theme_type = #{themeType}, is_active = #{isActive}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(DailyTheme dailyTheme);
    
    // 删除主题
    @Delete("DELETE FROM daily_themes WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    // 批量插入主题
    @Insert("<script>" +
            "INSERT INTO daily_themes (title, subtitle, image_url, theme_date, theme_type, is_active, created_at, updated_at) VALUES " +
            "<foreach collection='themes' item='theme' separator=','>" +
            "(#{theme.title}, #{theme.subtitle}, #{theme.imageUrl}, #{theme.themeDate}, #{theme.themeType}, #{theme.isActive}, #{theme.createdAt}, #{theme.updatedAt})" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("themes") List<DailyTheme> themes);
}