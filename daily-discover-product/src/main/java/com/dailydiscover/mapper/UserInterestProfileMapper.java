package com.dailydiscover.mapper;

import com.dailydiscover.model.UserInterestProfile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户兴趣画像表 Mapper
 */
@Mapper
public interface UserInterestProfileMapper extends BaseMapper<UserInterestProfile> {
    
    /**
     * 根据用户ID查询兴趣画像
     */
    @Select("SELECT * FROM user_interest_profiles WHERE user_id = #{userId}")
    UserInterestProfile findByUserId(@Param("userId") Long userId);
    
    /**
     * 根据兴趣标签推荐用户
     */
    @Select("SELECT user_id FROM user_interest_profiles WHERE interest_tags LIKE CONCAT('%', #{interestTags}, '%') LIMIT #{limit}")
    List<Long> findUsersByInterestTags(@Param("interestTags") String interestTags, @Param("limit") int limit);
}