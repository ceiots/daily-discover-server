package com.example.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.model.UserBrowsingHistory;

@Mapper
public interface UserBrowsingHistoryDao {
    
    /**
     * 根据用户ID查询浏览历史
     */
    @Select("SELECT * FROM user_browsing_history WHERE user_id = #{userId} ORDER BY browse_time DESC LIMIT 50")
    List<UserBrowsingHistory> findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和类别ID查询浏览历史
     */
    @Select("SELECT * FROM user_browsing_history WHERE user_id = #{userId} AND category_id = #{categoryId} ORDER BY browse_time DESC")
    List<UserBrowsingHistory> findByUserIdAndCategoryId(
        @Param("userId") Long userId, 
        @Param("categoryId") Long categoryId);
    
    @Insert("INSERT INTO user_browsing_history (user_id, product_id, category_id, browse_time, browse_count, created_at, updated_at) " +
            "VALUES (#{userId}, #{productId}, #{categoryId}, #{browseTime}, #{browseCount}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(UserBrowsingHistory history);

    @Update("UPDATE user_browsing_history SET browse_time = #{browseTime}, browse_count = #{browseCount}, " +
            "updated_at = #{updatedAt} WHERE id = #{id}")
    void update(UserBrowsingHistory history);

    @Select("SELECT * FROM user_browsing_history WHERE user_id = #{userId} AND product_id = #{productId}")
    UserBrowsingHistory findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
    
    // 其他方法...
} 