package com.dailydiscover.mapper;

import com.dailydiscover.model.RecommendationEffect;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 推荐效果追踪表 Mapper
 */
@Mapper
public interface RecommendationEffectMapper extends BaseMapper<RecommendationEffect> {
    
    /**
     * 根据推荐ID查询效果数据
     */
    @Select("SELECT * FROM recommendation_effects WHERE recommendation_id = #{recommendationId}")
    List<RecommendationEffect> findByRecommendationId(@Param("recommendationId") Long recommendationId);
    
    /**
     * 根据用户ID查询推荐效果
     */
    @Select("SELECT * FROM recommendation_effects WHERE user_id = #{userId} ORDER BY tracked_at DESC")
    List<RecommendationEffect> findByUserId(@Param("userId") Long userId);
}