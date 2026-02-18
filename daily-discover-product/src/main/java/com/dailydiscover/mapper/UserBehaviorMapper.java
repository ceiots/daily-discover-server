package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.UserBehaviorLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserBehaviorMapper extends BaseMapper<UserBehaviorLog> {
    
    /**
     * 根据用户ID查询行为记录
     */
    @Select("SELECT * FROM user_behavior_log WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY created_at DESC LIMIT #{limit}")
    List<UserBehaviorLog> findByUserId(@Param("userId") Long userId, @Param("limit") int limit);
    
    /**
     * 根据商品ID查询行为记录
     */
    @Select("SELECT * FROM user_behavior_log WHERE product_id = #{productId} AND is_deleted = 0 ORDER BY created_at DESC LIMIT #{limit}")
    List<UserBehaviorLog> findByProductId(@Param("productId") Long productId, @Param("limit") int limit);
    
    /**
     * 根据行为类型查询记录
     */
    @Select("SELECT * FROM user_behavior_log WHERE behavior_type = #{behaviorType} AND is_deleted = 0 ORDER BY created_at DESC LIMIT #{limit}")
    List<UserBehaviorLog> findByBehaviorType(@Param("behaviorType") String behaviorType, @Param("limit") int limit);
    
    /**
     * 统计用户行为次数
     */
    @Select("SELECT COUNT(*) FROM user_behavior_log WHERE user_id = #{userId} AND behavior_type = #{behaviorType} AND is_deleted = 0")
    int countUserBehavior(@Param("userId") Long userId, @Param("behaviorType") String behaviorType);
}