package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.model.Event;

@Mapper
public interface EventMapper {
    
    @Select("SELECT * FROM events WHERE DATE(event_date) = #{date}")
    List<Event> getEventsByDate(String date);

    @Select("SELECT * FROM events WHERE title LIKE CONCAT('%', #{keyword}, '%')")
    List<Event> searchEvents(String keyword);
}