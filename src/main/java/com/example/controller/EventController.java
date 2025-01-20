package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.mapper.EventMapper;
import com.example.model.Event;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventMapper eventMapper;

    @GetMapping("/date")
    public List<Event> getEventsByDate(@RequestParam String date) {
        return eventMapper.getEventsByDate(date);
    }
}