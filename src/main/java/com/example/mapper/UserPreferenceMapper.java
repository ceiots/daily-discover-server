package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.UserPreference;

/**
 * 用户偏好数据访问接口
 */
@Mapper
public interface UserPreferenceMapper extends BaseMapper<UserPreference> {
    
    /**
     * 根据用户ID查询用户偏好
     */
    @Select("SELECT * FROM user_preference WHERE user_id = #{userId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "preferenceLevel", column = "preference_level"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<UserPreference> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和类别ID查询用户偏好
     */
    @Select("SELECT * FROM user_preference WHERE user_id = #{userId} AND category_id = #{categoryId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "preferenceLevel", column = "preference_level"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<UserPreference> findByUserIdAndCategoryId(
            @Param("userId") Long userId, 
            @Param("categoryId") Long categoryId);
    
    /**
     * 插入用户偏好
     */
    @Insert("INSERT INTO user_preference(user_id, category_id, preference_level, create_time) " +
            "VALUES(#{userId}, #{categoryId}, #{preferenceLevel}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserPreference userPreference);
    
    /**
     * 更新用户偏好
     */
    @Update("UPDATE user_preference SET preference_level = #{preferenceLevel}, update_time = #{updateTime} " +
            "WHERE user_id = #{userId} AND category_id = #{categoryId}")
    int updatePreferenceLevel(
            @Param("userId") Long userId, 
            @Param("categoryId") Long categoryId, 
            @Param("preferenceLevel") Integer preferenceLevel,
            @Param("updateTime") java.util.Date updateTime);
    
    /**
     * 删除用户偏好
     */
    @Delete("DELETE FROM user_preference WHERE user_id = #{userId} AND category_id = #{categoryId}")
    int deleteByUserIdAndCategoryId(
            @Param("userId") Long userId, 
            @Param("categoryId") Long categoryId);
} 