package com.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ConfigMapper {
    @Select("SELECT value FROM config WHERE `key` = #{key}")
    String getConfigValue(String key);
}
