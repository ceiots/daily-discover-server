package com.example.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.example.model.Event;

@Mapper
public interface EventMapper {
    
    @Select("SELECT * FROM events limit 2")
    List<Event> getAllEvents();
}