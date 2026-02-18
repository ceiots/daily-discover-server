package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.ProductRecommendation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductRecommendationMapper extends BaseMapper<ProductRecommendation> {
    
    /**
     * 根据商品ID和推荐类型查询推荐列表
     */
    @Select("SELECT * FROM product_recommendations WHERE product_id = #{productId} AND recommendation_type = #{recommendationType} AND is_deleted = 0 ORDER BY sort_order ASC")
    List<ProductRecommendation> findByProductIdAndType(@Param("productId") Long productId, @Param("recommendationType") String recommendationType);
    
    /**
     * 根据用户ID查询个性化推荐
     */
    @Select("SELECT * FROM product_recommendations WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY score DESC LIMIT 20")
    List<ProductRecommendation> findByUserId(@Param("userId") Long userId);
    
    /**
     * 查询每日发现推荐
     */
    @Select("SELECT * FROM product_recommendations WHERE recommendation_type = 'daily_discover' AND is_deleted = 0 ORDER BY score DESC LIMIT 50")
    List<ProductRecommendation> findDailyDiscoverProducts();
    
    /**
     * 根据推荐类型查询热门推荐
     */
    @Select("SELECT * FROM product_recommendations WHERE recommendation_type = #{recommendationType} AND is_deleted = 0 ORDER BY score DESC LIMIT #{limit}")
    List<ProductRecommendation> findByTypeWithLimit(@Param("recommendationType") String recommendationType, @Param("limit") int limit);
}