package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.example.entity.UserBehaviorLog;

@Mapper
public interface UserBehaviorLogMapper {
    
    @Results(id = "warehouseResultMap", value = {
        @Result(property = "userId", column = "user_id"),
        @Result(property = "actionType", column = "action_type")
    })
    @Select("SELECT * FROM user_behavior_log")
    List<UserBehaviorLog> findAll();

    
    @ResultMap("warehouseResultMap")
    @Select("SELECT * FROM user_behavior_log WHERE user_id = #{userId}")
    List<UserBehaviorLog> findByUserId(Long userId);

    @Insert("INSERT INTO user_behavior_log (user_id, action_type, details) VALUES (#{userId}, #{actionType}, #{details})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserBehaviorLog log);
}