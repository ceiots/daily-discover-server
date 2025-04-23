package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.example.model.Event;

@Mapper
public interface EventMapper {
    
    /**
     * 公共结果映射，供所有查询方法使用
     */
    @Results(id = "eventResultMap", value = {
        @Result(property = "id", column = "id"),
        @Result(property = "title", column = "title"),
        @Result(property = "description", column = "description"),
        @Result(property = "category", column = "category"),
        @Result(property = "eventDate", column = "event_date"),
        @Result(property = "imageUrl", column = "image_url")
    })
    @Select("SELECT * FROM events WHERE id = #{id}")
    Event findById(Long id);
    
    /**
     * 根据日期查询事件
     */
    @Select("SELECT * FROM events WHERE DATE(event_date) = #{date}")
    @ResultMap("eventResultMap")
    List<Event> getEventsByDate(String date);

    /**
     * 根据关键词搜索事件
     */
    @Select("SELECT * FROM events WHERE title LIKE CONCAT('%', #{keyword}, '%')")
    @ResultMap("eventResultMap")
    List<Event> searchEvents(String keyword);
    
    /**
     * 查询所有事件，按日期倒序排列
     */
    @Select("SELECT * FROM events ORDER BY event_date DESC")
    @ResultMap("eventResultMap")
    List<Event> findAll();
}