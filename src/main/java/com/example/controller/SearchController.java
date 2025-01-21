package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import com.example.dto.SearchResults;
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

    @GetMapping("/all")
    public ResponseEntity<SearchResults> searchAll(@RequestParam String keyword) {
        try {
            List<Event> events = eventMapper.searchEvents(keyword);
            List<Recommendation> recommendations = recommendationMapper.searchRecommendations(keyword);
            
            // 返回事件和推荐内容，不合并 AI 数据
            SearchResults results = new SearchResults(events, recommendations); // 确保 SearchResults 构造函数正确
            return new ResponseEntity<>(results, HttpStatus.OK);
        } catch (Exception e) {
            // 记录异常信息
            System.err.println("Error fetching search results: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // 返回 500 错误
        }
    }
}

