package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.UserBehaviorLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserBehaviorLogMapper extends BaseMapper<UserBehaviorLog> {
    
    /**
     * 根据用户ID查询行为记录
     */
    @Select("SELECT * FROM user_behavior_logs WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{limit}")
    List<UserBehaviorLog> findByUserId(@Param("userId") Long userId, @Param("limit") int limit);
    
    /**
     * 根据商品ID查询行为记录
     */
    @Select("SELECT * FROM user_behavior_logs WHERE product_id = #{productId} ORDER BY created_at DESC LIMIT #{limit}")
    List<UserBehaviorLog> findByProductId(@Param("productId") Long productId, @Param("limit") int limit);
    
    /**
     * 根据行为类型查询记录
     */
    @Select("SELECT * FROM user_behavior_logs WHERE behavior_type = #{behaviorType} ORDER BY created_at DESC LIMIT #{limit}")
    List<UserBehaviorLog> findByBehaviorType(@Param("behaviorType") String behaviorType, @Param("limit") int limit);
    
    /**
     * 统计用户行为次数
     */
    @Select("SELECT COUNT(*) FROM user_behavior_logs WHERE user_id = #{userId} AND behavior_type = #{behaviorType}")
    int countUserBehavior(@Param("userId") Long userId, @Param("behaviorType") String behaviorType);
    
    /**
     * 记录用户行为
     */
    @org.apache.ibatis.annotations.Insert("INSERT INTO user_behavior_logs (user_id, product_id, behavior_type, session_id, created_at) " +
            "VALUES (#{userId}, #{productId}, #{behaviorType}, #{sessionId}, NOW())")
    int recordUserBehavior(@Param("userId") Long userId, @Param("productId") Long productId, 
                          @Param("behaviorType") String behaviorType, @Param("sessionId") String sessionId);
    
    /**
     * 获取用户行为历史
     */
    @Select("SELECT * FROM user_behavior_logs WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{limit}")
    List<UserBehaviorLog> getUserBehaviorHistory(@Param("userId") Long userId, @Param("limit") int limit);
    
    /**
     * 获取商品行为历史
     */
    @Select("SELECT * FROM user_behavior_logs WHERE product_id = #{productId} ORDER BY created_at DESC LIMIT #{limit}")
    List<UserBehaviorLog> getProductBehaviorHistory(@Param("productId") Long productId, @Param("limit") int limit);
    
    /**
     * 获取最近浏览的商品
     */
    @Select("SELECT DISTINCT product_id FROM user_behavior_logs WHERE user_id = #{userId} AND behavior_type = 'view' " +
            "ORDER BY created_at DESC LIMIT #{limit}")
    List<Long> getRecentlyViewedProducts(@Param("userId") Long userId, @Param("limit") int limit);
    
    /**
     * 获取热门商品
     */
    @Select("SELECT product_id, COUNT(*) as view_count FROM user_behavior_logs WHERE behavior_type = 'view' " +
            "GROUP BY product_id ORDER BY view_count DESC LIMIT #{limit}")
    List<Long> getPopularProducts(@Param("limit") int limit);
}