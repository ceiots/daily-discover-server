package com.dailydiscover.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dailydiscover.model.Seller;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 商家基础信息表 Mapper
 */
@Mapper
public interface SellerMapper extends BaseMapper<Seller> {
    
    /**
     * 根据商家名称查询商家
     */
    @Select("SELECT * FROM sellers WHERE name LIKE CONCAT('%', #{name}, '%') AND status = 'active' ORDER BY rating DESC")
    List<Seller> findByNameLike(@Param("name") String name);
    
    /**
     * 查询高评分商家
     */
    @Select("SELECT * FROM sellers WHERE rating >= #{minRating} AND status = 'active' ORDER BY rating DESC")
    List<Seller> findHighRatingSellers(@Param("minRating") Double minRating);
    
    /**
     * 查询优选商家
     */
    @Select("SELECT * FROM sellers WHERE is_premium = true AND status = 'active' ORDER BY rating DESC")
    List<Seller> findPremiumSellers();
    
    /**
     * 查询热门商家（按粉丝数量排序）
     */
    @Select("SELECT * FROM sellers WHERE status = 'active' ORDER BY followers_count DESC LIMIT #{limit}")
    List<Seller> findPopularSellers(@Param("limit") int limit);
    
    /**
     * 查询高销量商家
     */
    @Select("SELECT * FROM sellers WHERE status = 'active' ORDER BY monthly_sales DESC LIMIT #{limit}")
    List<Seller> findTopSellingSellers(@Param("limit") int limit);
    
    /**
     * 根据商家状态查询
     */
    @Select("SELECT * FROM sellers WHERE status = #{status} ORDER BY created_at DESC")
    List<Seller> findByStatus(@Param("status") String status);
}