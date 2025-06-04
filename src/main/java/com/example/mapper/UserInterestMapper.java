package com.example.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInterestMapper {
    
    /**
     * 统计用户兴趣标签数量
     */
    @Select("SELECT COUNT(*) FROM user_interests WHERE user_id = #{userId}")
    Integer countUserInterests(Long userId);
    
    /**
     * 删除用户所有兴趣标签
     */
    @Delete("DELETE FROM user_interests WHERE user_id = #{userId}")
    int deleteUserInterests(Long userId);
    
    /**
     * 插入用户兴趣标签
     */
    @Insert("INSERT INTO user_interests (user_id, tag_id) VALUES (#{userId}, #{tagId})")
    int insertUserInterest(Long userId, Long tagId);
    
    /**
     * 更新用户冷启动状态
     */
    @Update("INSERT INTO user_cold_start (user_id, is_completed) VALUES (#{userId}, #{completed}) " +
            "ON DUPLICATE KEY UPDATE is_completed = #{completed}, updated_at = NOW()")
    int updateColdStartStatus(Long userId, boolean completed);
}