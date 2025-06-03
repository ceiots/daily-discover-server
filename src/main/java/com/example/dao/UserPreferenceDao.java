package com.example.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.model.UserPreference;

@Mapper
public interface UserPreferenceDao {
    
    /**
     * 根据用户ID查询偏好
     */
    @Select("SELECT * FROM user_preference WHERE user_id = #{userId} ORDER BY preference_level DESC")
    List<UserPreference> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和类别ID查询偏好
     */
    @Select("SELECT * FROM user_preference WHERE user_id = #{userId} AND category_id = #{categoryId}")
    List<UserPreference> findByUserIdAndCategoryId(
        @Param("userId") Long userId, 
        @Param("categoryId") Long categoryId);
    
    /**
     * 插入用户偏好
     */
    @Insert("INSERT INTO user_preference (user_id, category_id, preference_level, created_at, updated_at) " +
            "VALUES (#{userId}, #{categoryId}, #{preferenceLevel}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(UserPreference preference);
    
    /**
     * 更新用户偏好
     */
    @Update("UPDATE user_preference SET preference_level = #{preferenceLevel}, updated_at = #{updatedAt} " +
            "WHERE id = #{id}")
    void update(UserPreference preference);
    
    /**
     * 删除用户所有偏好
     */
    @Delete("DELETE FROM user_preference WHERE user_id = #{userId}")
    void deleteByUserId(@Param("userId") Long userId);
} 