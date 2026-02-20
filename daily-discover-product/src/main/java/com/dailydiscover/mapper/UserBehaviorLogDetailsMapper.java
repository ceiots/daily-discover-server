package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.UserBehaviorLogDetails;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserBehaviorLogDetailsMapper extends BaseMapper<UserBehaviorLogDetails> {
    
    /**
     * 根据行为ID查询详情
     */
    @Select("SELECT * FROM user_behavior_logs_details WHERE id = #{id}")
    UserBehaviorLogDetails findByBehaviorId(@Param("id") Long id);
    
    /**
     * 批量查询行为详情
     */
    @Select("<script>" +
            "SELECT * FROM user_behavior_logs_details WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<UserBehaviorLogDetails> findDetailsByIds(@Param("ids") List<Long> ids);
    
    /**
     * 插入行为详情
     */
    @org.apache.ibatis.annotations.Insert("INSERT INTO user_behavior_logs_details (id, referrer_url, behavior_context, created_at) " +
            "VALUES (#{id}, #{referrerUrl}, #{behaviorContext}, NOW())")
    int insertDetails(@Param("id") Long id, @Param("referrerUrl") String referrerUrl, 
                     @Param("behaviorContext") String behaviorContext);
}