package com.example.service;

import com.example.mapper.RecommendationMapper;
import com.example.model.Recommendation;
import com.example.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationService {

    @Autowired
    private RecommendationMapper recommendationMapper;

    public Recommendation getRecommendationById(Long id) {
        return recommendationMapper.getRecommendationById(id);
    }

    public List<Comment> getCommentsByRecommendationId(Long recommendationId) {
        return recommendationMapper.getCommentsByRecommendationId(recommendationId);
    }
}