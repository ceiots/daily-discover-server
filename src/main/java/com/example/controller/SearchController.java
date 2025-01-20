package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.mapper.EventMapper;
import com.example.mapper.RecommendationMapper;
import com.example.model.Event;
import com.example.model.Recommendation;


@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private EventMapper eventMapper;

    @Autowired
    private RecommendationMapper recommendationMapper;

    @GetMapping("/events")
    public List<Event> searchEvents(@RequestParam String keyword) {
        return eventMapper.searchEvents(keyword); // 根据关键字搜索事件
    }

    @GetMapping("/recommendations")
    public List<Recommendation> searchRecommendations(@RequestParam String keyword) {
        return recommendationMapper.searchRecommendations(keyword); // 根据关键字搜索推荐
    }
} 