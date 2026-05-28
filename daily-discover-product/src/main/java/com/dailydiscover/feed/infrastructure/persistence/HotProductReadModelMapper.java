package com.dailydiscover.feed.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.feed.infrastructure.persistence.entity.HotProductReadModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface HotProductReadModelMapper extends BaseMapper<HotProductReadModel> {

    @Select("SELECT * FROM product_display_read_model WHERE status = 1 AND is_trending = 1 " +
            "ORDER BY hot_score DESC LIMIT #{limit} OFFSET #{offset}")
    List<HotProductReadModel> findTrendingProducts(@Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT * FROM product_display_read_model WHERE status = 1 AND is_new_arrival = 1 " +
            "ORDER BY hot_score DESC LIMIT #{limit} OFFSET #{offset}")
    List<HotProductReadModel> findNewArrivalProducts(@Param("limit") int limit, @Param("offset") int offset);

    @Select("SELECT * FROM product_display_read_model WHERE product_id = #{productId} AND status = 1")
    HotProductReadModel findByProductId(@Param("productId") Long productId);

    @Select("SELECT COUNT(*) FROM product_display_read_model WHERE status = 1 AND is_trending = 1")
    long countTrendingProducts();

    @Select("SELECT COUNT(*) FROM product_display_read_model WHERE status = 1 AND is_new_arrival = 1")
    long countNewArrivalProducts();
}