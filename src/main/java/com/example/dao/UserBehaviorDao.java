package com.example.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.example.model.UserBehavior;

@Mapper
public interface UserBehaviorDao {
    
    /**
     * 插入用户行为记录
     */
    @Insert("INSERT INTO user_behavior (user_id, product_id, category_id, behavior_type, behavior_time, " +
            "duration, extra_data, device_info, ip_address, behavior_score, created_at, updated_at) " +
            "VALUES (#{userId}, #{productId}, #{categoryId}, #{behaviorType}, #{behaviorTime}, " +
            "#{duration}, #{extraData}, #{deviceInfo}, #{ipAddress}, #{behaviorScore}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(UserBehavior behavior);
    
    /**
     * 根据用户ID查询行为记录
     */
    @Select("SELECT * FROM user_behavior WHERE user_id = #{userId} ORDER BY behavior_time DESC LIMIT #{limit}")
    List<UserBehavior> findByUserId(@Param("userId") Long userId, @Param("limit") int limit);
    
    /**
     * 根据用户ID和行为类型查询行为记录
     */
    @Select("SELECT * FROM user_behavior WHERE user_id = #{userId} AND behavior_type = #{behaviorType} " +
            "ORDER BY behavior_time DESC LIMIT #{limit}")
    List<UserBehavior> findByUserIdAndBehaviorType(
            @Param("userId") Long userId, 
            @Param("behaviorType") String behaviorType,
            @Param("limit") int limit);
    
    /**
     * 根据用户ID和时间范围查询行为记录
     */
    @Select("SELECT * FROM user_behavior WHERE user_id = #{userId} " +
            "AND behavior_time BETWEEN #{startTime} AND #{endTime} " +
            "ORDER BY behavior_time DESC")
    List<UserBehavior> findByUserIdAndTimeRange(
            @Param("userId") Long userId, 
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime);
    
    /**
     * 根据用户ID和类别ID查询行为记录
     */
    @Select("SELECT * FROM user_behavior WHERE user_id = #{userId} AND category_id = #{categoryId} " +
            "ORDER BY behavior_time DESC LIMIT #{limit}")
    List<UserBehavior> findByUserIdAndCategoryId(
            @Param("userId") Long userId, 
            @Param("categoryId") Long categoryId,
            @Param("limit") int limit);
    
    /**
     * 统计用户对某个类别的行为得分总和
     */
    @Select("SELECT SUM(behavior_score) FROM user_behavior " +
            "WHERE user_id = #{userId} AND category_id = #{categoryId}")
    Double sumBehaviorScoreByUserIdAndCategoryId(
            @Param("userId") Long userId, 
            @Param("categoryId") Long categoryId);
} 