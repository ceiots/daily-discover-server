package com.dailydiscover.mapper;

import com.dailydiscover.model.ScenarioRecommendation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 场景推荐表 Mapper
 */
@Mapper
public interface ScenarioRecommendationMapper extends BaseMapper<ScenarioRecommendation> {
    
    /**
     * 根据场景类型查询推荐商品
     */
    @Select("SELECT * FROM scenario_recommendations WHERE scenario_type = #{scenarioType} ORDER BY recommendation_score DESC")
    List<ScenarioRecommendation> findByScenarioType(@Param("scenarioType") String scenarioType);
    
    /**
     * 根据商品ID查询场景推荐
     */
    @Select("SELECT * FROM scenario_recommendations WHERE product_id = #{productId}")
    List<ScenarioRecommendation> findByProductId(@Param("productId") Long productId);
}