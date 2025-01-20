package com.example.mapper;

import com.example.model.Comment;
import com.example.model.Recommendation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface RecommendationMapper {
    @Select("SELECT * FROM recommendations")
    List<Recommendation> getAllRecommendations();

    // 根据 ID 查询推荐项
    @Select("SELECT * FROM recommendations WHERE id = #{id}")
    Recommendation getRecommendationById(Long id);

    @Select("SELECT * FROM comments WHERE recommendation_id = #{recommendationId}")
    List<Comment> getCommentsByRecommendationId(Long recommendationId);

    @Select("SELECT * FROM recommendations ORDER BY RAND() LIMIT #{limit}")
    List<Recommendation> getRandomRecommendations(@Param("limit") int limit);

    @Select("SELECT * FROM recommendations WHERE category_id = #{categoryId}")
    List<Recommendation> getRecommendationsByCategoryId(@Param("categoryId") Long categoryId);

}