package com.example.mapper;

import com.example.model.Recommendation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RecommendationMapper {
    @Select("SELECT * FROM recommendations")
    List<Recommendation> getAllRecommendations();
}