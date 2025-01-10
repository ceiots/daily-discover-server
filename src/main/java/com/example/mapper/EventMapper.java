package com.example.mapper;

import com.example.entity.Event;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EventMapper {
    
    @Select("SELECT * FROM events")
    List<Event> getAllEvents();
}