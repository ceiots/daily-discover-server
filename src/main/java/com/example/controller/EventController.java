package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.entity.Event;
import com.example.mapper.EventMapper;

@RestController
@RequestMapping("/dailyDiscover/events")
public class EventController {

    @Autowired
    private EventMapper eventMapper;

    @GetMapping("")
    public List<Event> getAllEvents() {
        return eventMapper.getAllEvents();
    }
}