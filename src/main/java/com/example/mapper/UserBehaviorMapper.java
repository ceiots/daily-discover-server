package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.*;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.model.UserBehavior;

/**
 * 用户行为数据访问接口
 */
@Mapper
public interface UserBehaviorMapper extends BaseMapper<UserBehavior> {
    
    /**
     * 根据用户ID和行为类型查询用户行为记录
     * @param userId 用户ID
     * @param behaviorType 行为类型（VIEW, CLICK, BUY等）
     * @param limit 最大记录数
     * @return 用户行为记录列表
     */
    @Select("SELECT * FROM user_behavior WHERE user_id = #{userId} AND behavior_type = #{behaviorType} " +
            "ORDER BY create_time DESC LIMIT #{limit}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "behaviorType", column = "behavior_type"),
        @Result(property = "behaviorTime", column = "behavior_time"),
        @Result(property = "createTime", column = "create_time")
    })
    List<UserBehavior> findByUserIdAndBehaviorType(
            @Param("userId") Long userId, 
            @Param("behaviorType") String behaviorType, 
            @Param("limit") Integer limit);
    
    /**
     * 插入用户行为记录
     */
    @Insert("INSERT INTO user_behavior(user_id, product_id, category_id, behavior_type, behavior_time, create_time) " +
            "VALUES(#{userId}, #{productId}, #{categoryId}, #{behaviorType}, #{behaviorTime}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserBehavior userBehavior);
    
    /**
     * 统计用户某类行为的次数
     */
    @Select("SELECT COUNT(*) FROM user_behavior WHERE user_id = #{userId} AND behavior_type = #{behaviorType}")
    int countByUserIdAndBehaviorType(
            @Param("userId") Long userId, 
            @Param("behaviorType") String behaviorType);
} 