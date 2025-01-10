package com.example.controller;

import com.example.mapper.RecommendationMapper;
import com.example.model.Recommendation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/daily-discover/recommendations")
public class RecommendationController {

    @Autowired
    private RecommendationMapper recommendationMapper;

    @GetMapping("")
    public List<Recommendation> getAllRecommendations() {
        return recommendationMapper.getAllRecommendations();
    }
}