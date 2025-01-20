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

    public List<Recommendation> getRecommendationsByCategoryId(Long categoryId) {
        return recommendationMapper.getRecommendationsByCategoryId(categoryId);
    }
    
    public List<Recommendation> getRandomRecommendations() {
        // 假设我们从数据库中随机获取 5 条推荐
        return recommendationMapper.getRandomRecommendations(6);
    }
}
